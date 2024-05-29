package com.kuzmich.searchengineapp.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;

class SiteMapperTest {

    private SiteMapper siteMapper;

    @BeforeEach
    void init() {
        siteMapper = new SiteMapper();

    }

    @Test
    void testMapToSite() {
    
        SiteObject siteObject = SiteObject.builder()
        .name("site")
        .url("url")
        .build();

        Site site = siteMapper.toSite(siteObject);

        assertEquals("url", site.getUrl());
        assertEquals("site", site.getName());
        assertEquals("NEW", site.getStatus().name());
        assertNull(site.getId());
    }

    @Test
    void testMapToSiteObject() {
        Site site = Site.builder()
                .id(1)
                .name("kuzmich")
                .url("kuzmich.com")
                .lastError("")
                .status(Status.INDEXED)
        .build();

        SiteObject siteObject = siteMapper.toSiteObject(site);
       
        assertEquals("kuzmich", siteObject.getName());
        assertEquals("kuzmich.com", siteObject.getUrl());
        
    }
}
