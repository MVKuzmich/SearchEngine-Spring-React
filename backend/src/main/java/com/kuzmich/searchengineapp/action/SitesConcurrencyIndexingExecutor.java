package com.kuzmich.searchengineapp.action;

import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.exception.IndexInterruptedException;
import com.kuzmich.searchengineapp.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class SitesConcurrencyIndexingExecutor {

    private final SiteConfig siteConfig;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final FieldRepository fieldRepository;
    private final Lemmatizator lemmatizator;

    @Setter
    @Getter
    private boolean isExecuting;

    public void executeSitesIndexing() throws IndexInterruptedException {
        List<SiteConfig.SiteObject> siteObjects = siteConfig.getSiteArray();
        ExecutorService pool = Executors.newFixedThreadPool(siteObjects.size());
        List<ForkJoinPool> fjPoolList = new ArrayList<>();
        try {
            long start = System.currentTimeMillis() / 1000;
            List<CompletableFuture<Integer>> futureList = new ArrayList<>();
            siteObjects.stream().map(siteObject -> {
                        Optional<Site> foundSite = siteRepository.findSiteByUrl(siteObject.getUrl());
                        foundSite.ifPresent(site -> siteRepository.removeSiteById(site.getId()));
                        WebSiteAnalyzer siteAnalyzer = new WebSiteAnalyzer(pageRepository, lemmaRepository, indexRepository, fieldRepository, siteRepository, siteConfig, lemmatizator);
                        Site site = getSite(siteObject);
                        siteAnalyzer.setSite(site);
                        siteAnalyzer.setMainPath(site.getUrl());
                        return siteAnalyzer;
                    })
                    .map(siteAnalyzer -> {
                        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                            ForkJoinPool fjPool = new ForkJoinPool();
                            fjPool.invoke(siteAnalyzer);
                            fjPoolList.add(fjPool);
                            return siteAnalyzer.getSite().getId();
                        }, pool);
                        futureList.add(future);
                        return future;
                    })
                    .forEach(future -> future.thenAccept(result -> {
                        if(isExecuting) {
                            siteRepository.updateSiteStatus(Status.INDEXED, result);
                        }
                    }));
            futureList.forEach(CompletableFuture::join);
            log.info("ИТОГО длительность ИНДЕКСАЦИИ: {} минут", (System.currentTimeMillis() / 1000 - start) / 60);
        } catch (Exception ex) {
            throw new IndexInterruptedException(ex.getMessage());
        } finally {
            fjPoolList.forEach(ForkJoinPool::shutdownNow);
            fjPoolList.clear();
            pool.shutdownNow();
        }
    }

    private Site getSite(SiteConfig.SiteObject siteObject) {
        Site site = new Site(
                Status.INDEXING,
                System.currentTimeMillis(),
                "",
                siteObject.getUrl(),
                siteObject.getName()
        );
        return siteRepository.saveAndFlush(site);
    }
}



