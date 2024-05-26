package com.kuzmich.searchengineapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.kuzmich.searchengineapp.dto.SiteObject;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "externaldata")
@Getter
@Setter
public class SiteConfig {
    private List<SiteObject> siteArray = new ArrayList<>();
    private String userAgent;
    private String referrer;

    
}