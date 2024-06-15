package com.kuzmich.searchengineapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.exception.SiteNotSaveException;
import com.kuzmich.searchengineapp.mapper.SiteMapper;
import com.kuzmich.searchengineapp.repository.SiteRepository;

@ExtendWith(MockitoExtension.class)
class IndexingServiceTest {

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private SiteMapper siteMapper;

    @InjectMocks
    private IndexingService indexingService;

    private SiteObject siteObject;

    @BeforeEach
    void init() {
        siteObject = SiteObject.builder()
            .name("name")
            .url("url")
            .build();
                
    }

    @Test
    void saveSite_successCase() {
        Site site = Site.builder()
                .id(1)
                .name("name")
                .url("url")
                .build();
        doReturn(site).when(siteRepository).save(any(Site.class));
        when(siteMapper.toSite(any(SiteObject.class))).thenCallRealMethod();
        
        ResultDTO dto = indexingService.saveSite(siteObject);
        
        assertTrue(dto.isResult());
        assertNull(dto.getError());
    }

    @Test
    void saveSite_throwException() {
        doThrow(new IllegalArgumentException("Test exception")).when(siteRepository).save(any(Site.class));
        when(siteMapper.toSite(any(SiteObject.class))).thenCallRealMethod();
        
        SiteNotSaveException exception = assertThrows(SiteNotSaveException.class,
                () -> indexingService.saveSite(siteObject));

        assertEquals("We have server problems, try it later!", exception.getMessage());
        assertEquals("Test exception", exception.getCause().getMessage());
        
    }

    @Test
    void saveSite_throwException_urlNotUnique() {
        doReturn(2L).when(siteRepository).countByUrl(any());

        SiteNotSaveException exception = assertThrows(SiteNotSaveException.class,
                () -> indexingService.saveSite(siteObject));

        assertEquals("Such an url has already existed", exception.getMessage());
        verify(siteRepository, never()).save(any(Site.class));

    }

    @Test
    void getSites() {
        Site site = Site.builder()
                .id(1)
                .name("name")
                .url("url")
                .build();

        doReturn(List.of(site)).when(siteRepository).findAll();
        when(siteMapper.toSiteObject(any(Site.class))).thenCallRealMethod();

        List<SiteObject> sites = indexingService.getSites();

        assertEquals(1, sites.size());
        assertEquals("name", sites.get(0).getName());
        assertEquals("url", sites.get(0).getUrl());
    }
}
