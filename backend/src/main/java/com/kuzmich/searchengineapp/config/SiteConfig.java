package com.kuzmich.searchengineapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "externaldata")
@Getter
@Setter
public class SiteConfig {
    private String userAgent;
    private String referrer;

    
}