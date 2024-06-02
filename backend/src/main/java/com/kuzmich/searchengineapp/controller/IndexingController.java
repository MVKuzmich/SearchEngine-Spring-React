package com.kuzmich.searchengineapp.controller;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.dto.statistics.Result;

import com.kuzmich.searchengineapp.service.IndexingService;
import com.kuzmich.searchengineapp.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@Log4j2
@CrossOrigin
public class IndexingController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;

    @GetMapping("/statistics")
    public Result getStatisticInformation() {
        log.info("просят статистику");
        return statisticsService.getStatisticInformation();
    }

    @GetMapping("/startIndexing")
    public ResultDTO startIndexing(String url) {
        indexingService.executeIndexation(url);
        return new ResultDTO(true);
    }

    @GetMapping("/stopIndexing")
    public ResultDTO stopIndexing() {
        log.info("ОСТАНОВКА ИНДЕКСАЦИИ - ОСТАНОВКА ИНДЕКСАЦИИ - ОСТАНОВКА ИНДЕКСАЦИИ");
        return indexingService.stopIndexation();
    }

    @PostMapping("/addSite")
    public ResultDTO addSite(@RequestBody SiteObject siteObject) {

        return indexingService.saveSite(siteObject);

    }

    @PostMapping("/deleteSite") 
    public ResultDTO deleteSite(@RequestBody SiteObject site) {
        
        return indexingService.deleteSite(site);
    }
    

    @GetMapping("/sites")
    public List<SiteObject> getSites() {
        return indexingService.getSites();
    }

    @PostMapping("/indexPage")
    public ResultDTO executePageIndexing(@RequestParam("url") String url) {
        return indexingService.executePageIndexation(url);
    }

}
