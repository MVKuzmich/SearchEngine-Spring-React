package com.kuzmich.searchengineapp.service;

import com.kuzmich.searchengineapp.action.Lemmatizator;
import com.kuzmich.searchengineapp.action.SitesConcurrencyIndexingExecutor;
import com.kuzmich.searchengineapp.action.WebSiteAnalyzer;
import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.*;
import com.kuzmich.searchengineapp.exception.IndexExecutionException;
import com.kuzmich.searchengineapp.exception.IndexInterruptedException;
import com.kuzmich.searchengineapp.exception.SiteNotFoundException;
import com.kuzmich.searchengineapp.exception.SiteNotSaveException;
import com.kuzmich.searchengineapp.mapper.SiteMapper;
import com.kuzmich.searchengineapp.repository.*;
import com.kuzmich.searchengineapp.utils.HashGenerator;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class IndexingService {

    private final SitesConcurrencyIndexingExecutor indexingExecutor;
    private final SiteConfig siteConfig;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final IndexRepository indexRepository;
    private final Lemmatizator lemmatizator;
    private final SiteMapper siteMapper;

    @Async
    public void startIndexation(List<SiteObject> sites) throws IndexExecutionException, IndexInterruptedException {
        if (indexingExecutor.isExecuting()) {
            throw new IndexExecutionException("Индексация уже запущена");
        } else {
            try {
                indexingExecutor.setExecuting(true);
                WebSiteAnalyzer.setIndexationStopped(false);
                indexingExecutor.executeSitesIndexing(sites);
            } catch (IndexInterruptedException ex) {
                WebSiteAnalyzer.setIndexationStopped(true);
                updateSiteStatus(ex.getMessage(), Status.FAILED);
                log.error(Arrays.toString(ex.getStackTrace()));
                throw ex;
            } finally {
                WebSiteAnalyzer.getServiceSet().clear();
                indexingExecutor.setExecuting(false);
            }
        }
    }

    public ResultDTO stopIndexation() throws IndexExecutionException {
        if (!indexingExecutor.isExecuting()) {
            throw new IndexExecutionException(new ResultDTO(false, "Индексация не запущена").getError());
        } else {
            indexingExecutor.setExecuting(false);
            WebSiteAnalyzer.setIndexationStopped(true);
            updateSiteStatus("Индексация остановлена пользователем!", Status.STOPPED);
            return new ResultDTO(false);
        }
    }

    // public ResultDTO executePageIndexation(String pageUrl) throws IndexExecutionException {
    //     List<SiteObject> siteObjects = siteConfig.getSiteArray();
    //     Optional<SiteObject> siteObjectOptional = siteObjects.stream()
    //             .filter(siteObject -> pageUrl.startsWith(siteObject.getUrl()))
    //             .findFirst();
    //     if (siteObjectOptional.isEmpty()) {
    //         throw new IndexExecutionException(new ResultDTO(false,
    //                 "Данная страница находится за пределами сайтов, указанных в конфигурационном файле")
    //                 .getError());
    //     } else {
    //         String url = siteObjectOptional.get().getUrl();
    //         Site site = siteRepository.findSiteByUrl(url).get();
    //         WebSiteAnalyzer.setOnePageIndexation(true);
    //         try {
    //             updateDataIfReindexExecute(pageUrl, site.getId());
    //         } catch (RuntimeException ex) {
    //             log.info(ex.getMessage());
    //         }
    //         WebSiteAnalyzer wsa = new WebSiteAnalyzer(pageRepository, lemmaRepository, indexRepository, fieldRepository,
    //                 siteRepository, siteConfig, lemmatizator);
    //         wsa.setSite(site);
    //         wsa.setMainPath(pageUrl);
    //         new ForkJoinPool().invoke(wsa);
    //         WebSiteAnalyzer.setOnePageIndexation(false);
    //         log.info("Переиндексация завершена");
    //         return new ResultDTO(true);
    //     }
    // }

    public ResultDTO saveSite(SiteObject siteObject) throws SiteNotSaveException {
        try {
            if (siteRepository.countByUrl(siteObject.getUrl()) >= 1) {
                throw new SiteNotSaveException("Such an url has already existed");
            }

            Site siteEntity = siteMapper.toSite(siteObject);
            siteRepository.save(siteEntity);

            return new ResultDTO(true);

        } catch (IllegalArgumentException | OptimisticLockingFailureException | InternalError ex) {
            throw new SiteNotSaveException("We have server problems, try it later!", ex);
        }
    }

    public List<SiteObject> getSites() {
        return siteRepository.findAll()
                .stream()
                .map(siteMapper::toSiteObject)
                .collect(Collectors.toList());
    }

    public List<SiteObject> getSitesByStatus(Status statusName) {
        return siteRepository.findAllByStatus(statusName).stream()
                .map(siteMapper::toSiteObject)
                .toList();
    }

    public ResultDTO deleteSite(SiteObject site) {
        Optional<Site> siteOptional = siteRepository.findSiteByUrl(site.getUrl());
        if (siteOptional.isEmpty()) {
            throw new SiteNotFoundException(String.format("The site %s is not found", site.getUrl()));
        }
        siteRepository.delete(siteOptional.get());

        return new ResultDTO(true);
    }

    public ResultDTO deleteIndexationResults(List<SiteObject> siteObjects) {
        if(siteObjects == null || siteObjects.isEmpty()) {
            throw new IndexExecutionException("List of sites is empty or do not exist");
        }

        siteObjects.forEach(site -> {
                Optional<Site> siteOptional = siteRepository.findSiteByUrl(site.getUrl());
                if(siteOptional.isEmpty()) {
                    throw new IndexExecutionException(String.format("The site is not found by the url: %s", site.getUrl()));
                }
                siteRepository.delete(siteOptional.get());
            
            });
    

        return new ResultDTO(true);
    }

    private void updateSiteStatus(String message, Status status) {
        List<Site> sites = siteRepository.findAll().stream().filter(site -> site.getStatus() == Status.INDEXING)
                .collect(Collectors.toList());
        if (!sites.isEmpty()) {
            sites.stream().mapToInt(Site::getId)
                    .forEach(id -> siteRepository.updateSiteStatusAndError(status, message, id));
        }
    }

    // private void updateDataIfReindexExecute(String pageUrl, int siteId) throws RuntimeException {
    //     String regex = "https?:\\/\\/\\w+\\.\\w+(\\/.*)";
    //     Matcher matcher = Pattern.compile(regex).matcher(pageUrl);
    //     String url = "";
    //     if (matcher.find()) {
    //         url = matcher.group(1);
    //     }
    //     Page page = pageRepository.findPageByPath(url.concat("/"), siteId)
    //             .orElseThrow(() -> new RuntimeException("Url is not exist! Indexation is started"));
    //     List<Index> indexList = page.getIndexList();
    //     pageRepository.deleteById(page.getId());
    //     indexList.stream()
    //             .map(Index::getLemma)
    //             .map(Lemma::getId)
    //             .forEach(lemmaRepository::minusLemmaFrequencyById);
    // }
}
