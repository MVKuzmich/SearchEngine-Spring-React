package com.kuzmich.searchengineapp.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;

public class SiteMapperTest {

    private SiteMapper siteMapper;

    @BeforeEach
    void init() {
        siteMapper = new SiteMapper();

    }

    @Test
    public void testMapToMyEntity() {
    
        SiteObject siteObject = new SiteObject("url", "site");

        Site site = siteMapper.toSite(siteObject);

        assertEquals("url", site.getUrl());
        assertEquals("site", site.getName());
        assertEquals("NEW", site.getStatus().name());
        assertNull(site.getId());
    }
    
}
