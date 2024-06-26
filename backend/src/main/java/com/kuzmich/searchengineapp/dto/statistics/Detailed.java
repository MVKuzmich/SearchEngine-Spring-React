package com.kuzmich.searchengineapp.dto.statistics;

import com.kuzmich.searchengineapp.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@Builder
public class Detailed {

    private String url;
    private String name;
    private String hash;
    private Status status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;
}
