package com.kuzmich.searchengineapp.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SiteObject {
    String url;
    String name;

}
