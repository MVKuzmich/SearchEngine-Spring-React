package com.kuzmich.searchengineapp.service;

import com.kuzmich.searchengineapp.action.SearchPageExecution;
import com.kuzmich.searchengineapp.dto.searchDto.SearchPageData;
import com.kuzmich.searchengineapp.dto.searchDto.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchPageExecution searchPageExecution;

    private List<SearchPageData> resultList = new ArrayList<>();
    @Setter
    private String query;


    public SearchResult getSearchResult(String userQuery, Integer offset, Integer limit, String site) {
        if(query != null && !query.equals(userQuery)) {
            resultList.clear();
        }
        if(resultList.isEmpty()) {
            setQuery(userQuery);
            resultList = searchPageExecution.getSearchResultListFromUserQuery(query, site);
        }
        return SearchResult
                .builder()
                .result(true)
                .count(resultList.size())
                .data(resultList.subList(offset, Math.min(resultList.size(), offset + limit)))
                .build();

    }

}
