package com.kuzmich.searchengineapp.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Total {

    private int sites;
    private int pages;
    private int lemmas;
    @JsonProperty("isIndexing")
    private boolean isIndexing;
}
