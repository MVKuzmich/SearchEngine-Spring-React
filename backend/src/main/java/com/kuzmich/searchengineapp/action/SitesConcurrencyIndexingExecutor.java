package com.kuzmich.searchengineapp.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.springframework.stereotype.Component;

import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.exception.IndexInterruptedException;
import com.kuzmich.searchengineapp.exception.SiteNotFoundException;
import com.kuzmich.searchengineapp.repository.FieldRepository;
import com.kuzmich.searchengineapp.repository.IndexRepository;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.PageRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

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

    
    public void executeSitesIndexing(List<SiteObject> siteObjects) throws IndexInterruptedException {       
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<ForkJoinPool> fjPoolList = new ArrayList<>();
        try {
            long start = System.currentTimeMillis() / 1000;
            List<CompletableFuture<Integer>> futureList = new ArrayList<>();
            siteObjects.stream().map(siteObject -> {
                        Optional<Site> siteOptional = siteRepository.findSiteByUrl(siteObject.getUrl());
                        if(siteOptional.isEmpty()) {
                            throw new SiteNotFoundException(String.format("No site was found by url %s", siteObject.getUrl()));
                        }
                        Site site = siteOptional.get();
                        WebSiteAnalyzer siteAnalyzer = new WebSiteAnalyzer(pageRepository, lemmaRepository, indexRepository, fieldRepository, siteRepository, siteConfig, lemmatizator);
                        siteAnalyzer.setSite(site);
                        siteAnalyzer.setMainPath(site.getUrl());
                        siteRepository.updateSiteStatus(Status.INDEXING, site.getId());
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
}



