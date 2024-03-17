package com.kuzmich.searchengineapp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Result {
    public boolean result;
    public Statistics statistics;
}
