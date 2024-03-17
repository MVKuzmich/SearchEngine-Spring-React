package com.kuzmich.searchengineapp.controller;

import com.kuzmich.searchengineapp.action.SitesConcurrencyIndexingExecutor;
import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.searchDto.SearchResult;
import com.kuzmich.searchengineapp.exception.EmptySearchQueryException;
import com.kuzmich.searchengineapp.exception.IndexExecutionException;
import com.kuzmich.searchengineapp.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final SitesConcurrencyIndexingExecutor indexingExecutor;

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<SearchResult> getSearchResult(@RequestParam("query") String query,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit,
                                                        @RequestParam(value = "site", required = false) String site) {
        if (query.isBlank()) {
            throw new EmptySearchQueryException(
                    new ResultDTO(false, "Задан пустой поисковый запрос").getError());
        } else if(indexingExecutor.isExecuting()) {
            throw new IndexExecutionException(new ResultDTO(false, "Индексация сайта не произведена").getError());
        }
        else {
            SearchResult result = searchService.getSearchResult(query, offset, limit, site);
            return ResponseEntity.ok(result);
        }
    }
}
