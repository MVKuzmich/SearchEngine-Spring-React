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

    @Test
    void throwExceptionIfSaveSite() {
        SiteObject siteObject = SiteObject.builder()
                .name("name")
                .url("url")
                .build();

        lenient().when(siteRepository.save(any(Site.class))).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(SiteNotSaveException.class, () -> indexingService.saveSite(siteObject));
        assertEquals("The site info is not saved for server causes! Try it later!", exception.getMessage());

        verify(siteRepository, never()).save(any(Site.class));
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

        ResultDTO dto = indexingService.saveSite(
                SiteObject.builder()
                        .name("name")
                        .url("url")
                        .build());
        assertTrue(dto.isResult());
        assertNull(dto.getError());
    }

    @Test
    void saveSite_throwException() {
        when(siteMapper.toSite(any(SiteObject.class))).thenCallRealMethod();
        doThrow(new RuntimeException("Test exception")).when(siteRepository).save(any(Site.class));

        SiteNotSaveException exception = assertThrows(SiteNotSaveException.class,
                () -> indexingService.saveSite(SiteObject.builder()
                        .name("name")
                        .url("url")
                        .build()));

        assertEquals("The site info is not saved for server causes! Try it later!", exception.getMessage());
        assertEquals("Test exception", exception.getCause().getMessage());
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

        assertEquals(1, indexingService.getSites().size());
        assertEquals("name", indexingService.getSites().get(0).getName());
        assertEquals("url", indexingService.getSites().get(0).getUrl());
    }
}
