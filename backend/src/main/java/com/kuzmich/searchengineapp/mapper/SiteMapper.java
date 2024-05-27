package com.kuzmich.searchengineapp.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;

@Component
public class SiteMapper {
    

    public Site toSite(SiteObject siteObject) {
        return Site.builder()
            .status(Status.NEW)
            .statusTime(getCurrentTimestampMillis())
            .lastError("")
            .url(siteObject.getUrl())
            .name(siteObject.getName())
        .build();
    }

    public SiteObject toSiteObject(Site site) {
        return SiteObject.builder()
           .url(site.getUrl())
           .name(site.getName())
        .build();
    }

    private long getCurrentTimestampMillis() {
        return Instant.now().toEpochMilli();
    }
}
