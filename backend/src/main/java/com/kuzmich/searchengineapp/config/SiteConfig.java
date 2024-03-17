package com.kuzmich.searchengineapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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

    @Setter
    @Getter
    public static class SiteObject {

        private String url;
        private String name;
    }
}