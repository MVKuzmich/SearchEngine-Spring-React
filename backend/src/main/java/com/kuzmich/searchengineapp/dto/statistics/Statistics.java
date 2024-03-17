package com.kuzmich.searchengineapp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Statistics {

    public Total total;
    public ArrayList<Detailed> detailed;
}
