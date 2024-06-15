package com.kuzmich.searchengineapp.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.kuzmich.searchengineapp.BaseTestIntegrationClass;
import com.kuzmich.searchengineapp.action.Lemmatizator;
import com.kuzmich.searchengineapp.action.SitesConcurrencyIndexingExecutor;
import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.mapper.SiteMapper;
import com.kuzmich.searchengineapp.repository.FieldRepository;
import com.kuzmich.searchengineapp.repository.IndexRepository;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.PageRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexingServiceIT extends BaseTestIntegrationClass {

    @MockBean
    private SitesConcurrencyIndexingExecutor indexingExecutor;
    @MockBean
    private SiteConfig siteConfig;
    @SpyBean
    private SiteRepository siteRepository;
    @MockBean
    private PageRepository pageRepository;
    @MockBean
    private LemmaRepository lemmaRepository;
    @MockBean
    private FieldRepository fieldRepository;
    @MockBean
    private IndexRepository indexRepository;
    @MockBean
    private Lemmatizator lemmatizator;
    
    
    private final SiteMapper siteMapper;
    
    private final IndexingService indexingService;

    @Test
    void saveSite_successCase() {
        SiteObject site = SiteObject.builder()
                                .name("name")
                                .url("url")
                            .build();
        doReturn(0L).when(siteRepository).countByUrl(site.getUrl());

        ResultDTO result = indexingService.saveSite(site);

        assertTrue(result.isResult());



    }

    
}
