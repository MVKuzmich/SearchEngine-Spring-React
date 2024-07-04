package com.kuzmich.searchengineapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.searchDto.SearchResult;
import com.kuzmich.searchengineapp.exception.EmptySearchQueryException;
import com.kuzmich.searchengineapp.service.SearchService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@CrossOrigin
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<SearchResult> getSearchResult(@RequestParam("query") String query,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit,
                                                        @RequestParam(value = "site", required = false) String site) {
        if (query.isBlank()) {
            throw new EmptySearchQueryException(
                    new ResultDTO(false, "Задан пустой поисковый запрос").getError());
        }else {
            SearchResult result = searchService.getSearchResult(query, offset, limit, site);
            return ResponseEntity.ok(result);
        }
    }
}
