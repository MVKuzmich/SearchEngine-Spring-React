package com.kuzmich.searchengineapp.action;

import com.kuzmich.searchengineapp.entity.*;
import com.kuzmich.searchengineapp.repository.FieldRepository;
import com.kuzmich.searchengineapp.repository.IndexRepository;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class PageIndexExecutor {

    private final FieldRepository fieldRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final Lemmatizator lemmatizator;

    public void executePageIndexing(Page page, Document htmlDocument) throws IOException {
            if (!WebSiteAnalyzer.isIndexationStopped()) {
                log.info("Индексируется {}", page.getPath());
                long start = System.currentTimeMillis() / 1_000L;
                List<Index> indexList = new ArrayList<>();
                Site site = page.getSite();
                for (Field field : fieldRepository.findAll()) {
                    String wordsFromPageTagContent = htmlDocument.getElementsByTag(field.getSelector()).text().toLowerCase();
                    Map<String, Integer> lemmasFromPageTagWords = lemmatizator.getLemmaList(wordsFromPageTagContent);
                    float lemmaRankByTag;
                    for (Map.Entry<String, Integer> lemmaItem : lemmasFromPageTagWords.entrySet()) {
                        Lemma lemma = saveLemma(lemmaItem, site);
                        lemmaRankByTag = field.getWeight() * lemmaItem.getValue();
                        int lemmaIdForIndexSearch = lemma.getId();
                        Optional<Index> indexOptional = indexList.stream().filter(i -> i.getLemma().getId() == lemmaIdForIndexSearch).findFirst();
                        if (indexOptional.isEmpty()) {
                            indexList.add(new Index(page, lemma, lemmaRankByTag));
                        } else {
                            Index index = indexOptional.get();
                            index.setRank(index.getRank() + lemmaRankByTag);
                        }
                    }
                }
                indexRepository.saveAllAndFlush(indexList);
                siteRepository.updateSiteStatusTime(System.currentTimeMillis(), site.getId());

                long end = System.currentTimeMillis() / 1_000L;
                long indexPageDuration = end - start;
                log.info("ИНДЕКСАЦИЯ СТРАНИЦЫ: id - {}, path - {}, ДЛИТЕЛЬНОСТЬ: {}", page.getId(), page.getPath(), indexPageDuration);
            }
    }

    private Lemma saveLemma(Map.Entry<String, Integer> lemmaItem, Site site) {
        Lemma lemma;
        synchronized (lemmaRepository) {
            Optional<Lemma> lemmaOptional = lemmaRepository.findLemmaObjectByLemmaNameAndSiteId(lemmaItem.getKey(), site.getId());
            if (lemmaOptional.isEmpty()) {
                lemma = lemmaRepository.saveAndFlush(
                        new Lemma(lemmaItem.getKey(), 1, site));
            } else {
                lemma = lemmaOptional.get();
                lemma.setFrequency(lemma.getFrequency() + 1);
                lemma = lemmaRepository.saveAndFlush(lemma);
            }
        }
        return lemma;
    }
}

