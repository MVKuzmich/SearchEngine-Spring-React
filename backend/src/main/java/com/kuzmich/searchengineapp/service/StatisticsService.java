package com.kuzmich.searchengineapp.service;

import com.kuzmich.searchengineapp.action.SitesConcurrencyIndexingExecutor;
import com.kuzmich.searchengineapp.dto.statistics.Detailed;
import com.kuzmich.searchengineapp.dto.statistics.Result;
import com.kuzmich.searchengineapp.dto.statistics.Statistics;
import com.kuzmich.searchengineapp.dto.statistics.Total;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.PageRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final SitesConcurrencyIndexingExecutor indexingExecutor;


    public Result getStatisticInformation() {
        List<Site> siteList = siteRepository.findAll();
        return Result
                .builder()
                .result(true)
                .statistics(getStatistics(siteList))
                .build();
    }

    public Statistics getStatistics(List<Site> sites) {
        return Statistics
                .builder()
                .total(getTotalStatistics(sites))
                .detailed((ArrayList<Detailed>) getDetailedSiteStatisticsList(sites))
                .build();
    }

    public Total getTotalStatistics(List<Site> sites) {
        List<Detailed> detailedSiteStatisticsList = getDetailedSiteStatisticsList(sites);

        return Total
                .builder()
                .sites(detailedSiteStatisticsList.size())
                .pages(detailedSiteStatisticsList.stream()
                        .mapToInt(Detailed::getPages)
                        .sum())
                .lemmas(detailedSiteStatisticsList.stream()
                        .mapToInt(Detailed::getLemmas)
                        .sum())
                .isIndexing(indexingExecutor.isExecuting())
                .build();
    }

    public Detailed getDetailedSiteStatistic(Site site) {
        return Detailed.builder()
                .url(site.getUrl())
                .name(site.getName())
                .status(site.getStatus())
                .statusTime(site.getStatusTime())
                .error(site.getLastError())
                .pages(pageRepository.countPageBySiteId(site.getId()))
                .lemmas(lemmaRepository.countLemmaBySiteId(site.getId()))
                .build();
    }

    public List<Detailed> getDetailedSiteStatisticsList(List<Site> siteList) {
        return siteList.stream()
                .map(this::getDetailedSiteStatistic)
                .collect(Collectors.toList());

    }

}
