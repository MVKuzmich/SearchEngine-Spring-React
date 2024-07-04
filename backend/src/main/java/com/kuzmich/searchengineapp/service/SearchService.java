package com.kuzmich.searchengineapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kuzmich.searchengineapp.action.SearchPageExecution;
import com.kuzmich.searchengineapp.dto.searchDto.SearchPageData;
import com.kuzmich.searchengineapp.dto.searchDto.SearchResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchPageExecution searchPageExecution;

    public SearchResult getSearchResult(String userQuery, Integer offset, Integer limit, String site) {

        List<SearchPageData>resultList = searchPageExecution.getSearchResultListFromUserQuery(userQuery, site);
        return SearchResult
                .builder()
                .result(true)
                .count(resultList.size())
                .data(resultList)
                .build();

    }

}
