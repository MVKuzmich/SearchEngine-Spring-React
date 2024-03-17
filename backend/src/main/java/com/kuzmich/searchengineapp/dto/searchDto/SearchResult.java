package com.kuzmich.searchengineapp.dto.searchDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchResult {
    public boolean result;
    public int count;
    public List<SearchPageData> data;
}
