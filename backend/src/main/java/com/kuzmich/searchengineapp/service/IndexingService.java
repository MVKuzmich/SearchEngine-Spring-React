package com.kuzmich.searchengineapp.service;

import com.kuzmich.searchengineapp.action.Lemmatizator;
import com.kuzmich.searchengineapp.action.SitesConcurrencyIndexingExecutor;
import com.kuzmich.searchengineapp.action.WebSiteAnalyzer;
import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.entity.*;
import com.kuzmich.searchengineapp.exception.IndexExecutionException;
import com.kuzmich.searchengineapp.exception.IndexInterruptedException;
import com.kuzmich.searchengineapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    public ResultDTO executeIndexation() throws IndexExecutionException, IndexInterruptedException {
        if (indexingExecutor.isExecuting()) {
            throw new IndexExecutionException(new ResultDTO(false, "Индексация уже запущена").getError());
        } else {
            String exceptionMessage = "";
            try {
                indexingExecutor.setExecuting(true);
                WebSiteAnalyzer.setIndexationStopped(false);
                indexingExecutor.executeSitesIndexing();
                return new ResultDTO(true);
            } catch (IndexInterruptedException ex) {
                WebSiteAnalyzer.setIndexationStopped(true);
                exceptionMessage = ex.getMessage();
                log.error(Arrays.toString(ex.getStackTrace()));
                throw ex;
            } finally {
                if (!exceptionMessage.isBlank()) {
                    updateSiteStatus(exceptionMessage);
                }
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
            updateSiteStatus("Индексация остановлена пользователем!");
            return new ResultDTO(true);
        }
    }

    public ResultDTO executePageIndexation(String pageUrl) throws IndexExecutionException {
        List<SiteConfig.SiteObject> siteObjects = siteConfig.getSiteArray();
        Optional<SiteConfig.SiteObject> siteObjectOptional = siteObjects.stream()
                .filter(siteObject -> pageUrl.startsWith(siteObject.getUrl()))
                .findFirst();
        if (siteObjectOptional.isEmpty()) {
            throw new IndexExecutionException(new ResultDTO(false, "Данная страница находится за пределами сайтов, указанных в конфигурационном файле")
                    .getError());
        } else {
            String url = siteObjectOptional.get().getUrl();
            Site site = siteRepository.findSiteByUrl(url).get();
            WebSiteAnalyzer.setOnePageIndexation(true);
            try {
                updateDataIfReindexExecute(pageUrl, site.getId());
            } catch (RuntimeException ex) {
                log.info(ex.getMessage());
            }
            WebSiteAnalyzer wsa = new WebSiteAnalyzer(pageRepository, lemmaRepository, indexRepository, fieldRepository, siteRepository, siteConfig, lemmatizator);
            wsa.setSite(site);
            wsa.setMainPath(pageUrl);
            new ForkJoinPool().invoke(wsa);
            WebSiteAnalyzer.setOnePageIndexation(false);
            log.info("Переиндексация завершена");
            return new ResultDTO(true);
        }
    }

    private void updateSiteStatus(String message) {
        List<Site> sites = siteRepository.findAll().stream().filter(site -> site.getStatus() != Status.INDEXED).collect(Collectors.toList());
        if (!sites.isEmpty()) {
            sites.stream().mapToInt(Site::getId).forEach(id -> siteRepository.updateSiteStatusAndError(Status.FAILED, message, id));
        }
    }

    private void updateDataIfReindexExecute(String pageUrl, int siteId) throws RuntimeException {
        String regex = "https?:\\/\\/\\w+\\.\\w+(\\/.*)";
        Matcher matcher = Pattern.compile(regex).matcher(pageUrl);
        String url = "";
        if (matcher.find()) {
            url = matcher.group(1);
        }
        Page page = pageRepository.findPageByPath(url.concat("/"), siteId).orElseThrow(() -> new RuntimeException("Url is not exist! Indexation is started"));
        List<Index> indexList = page.getIndexList();
        pageRepository.deleteById(page.getId());
        indexList.stream()
                .map(Index::getLemma)
                .map(Lemma::getId)
                .forEach(lemmaRepository::minusLemmaFrequencyById);
    }
}

