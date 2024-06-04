package com.kuzmich.searchengineapp.controller;

import com.kuzmich.searchengineapp.dto.ResultDTO;
import com.kuzmich.searchengineapp.dto.SiteObject;
import com.kuzmich.searchengineapp.dto.statistics.Result;
import com.kuzmich.searchengineapp.entity.Status;
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

    @GetMapping("/start-indexation")
    public ResultDTO startIndexing(String url) {
        indexingService.executeIndexation(url);
        return new ResultDTO(true);
    }

    @GetMapping("/stop-indexation")
    public ResultDTO stopIndexing() {
        log.info("ОСТАНОВКА ИНДЕКСАЦИИ - ОСТАНОВКА ИНДЕКСАЦИИ - ОСТАНОВКА ИНДЕКСАЦИИ");
        return indexingService.stopIndexation();
    }

    @PostMapping("/add-site")
    public ResultDTO addSite(@RequestBody SiteObject siteObject) {

        return indexingService.saveSite(siteObject);

    }

    @PostMapping("/delete-site") 
    public ResultDTO deleteSite(@RequestBody SiteObject site) {
        
        return indexingService.deleteSite(site);
    }
    

    @GetMapping("/new-sites")
    public List<SiteObject> getNewAddedSites() {
        return indexingService.getSitesByStatus(Status.NEW);
    }

    @PostMapping("/index-page")
    public ResultDTO executePageIndexing(@RequestParam("url") String url) {
        return indexingService.executePageIndexation(url);
    }

}
