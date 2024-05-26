package com.kuzmich.searchengineapp.action;


import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;


@Component
public class Lemmatizator {

    private final LuceneMorphology luceneMorphology;

    public Lemmatizator(LuceneMorphology luceneMorphology) {
        this.luceneMorphology = luceneMorphology;
    }

    public Map<String, Integer> getLemmaList(String text) throws IOException {
        Map<String, Integer> lemmaMap = new HashMap<>();
        Set<String> foundWords = new HashSet<>();
        Collections.addAll(foundWords, text.split("\\s"));
        String wordRegexForWord = "\"?[А-яЁё]+(?:-\1)?[\".?!,:;']?";
        for (String word : foundWords) {
            if (word.matches(wordRegexForWord)) {
                word = word.replaceAll("[\".?!,:;']", "");
                List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
                if (wordBaseForms.stream().anyMatch(w -> !w.matches(".+?ПРЕДЛ|СОЮЗ|ЧАСТ|МЕЖД.+?"))) {
                    luceneMorphology.getNormalForms(word)
                            .forEach(w -> {
                                lemmaMap.computeIfPresent(w, (key, value) -> value + 1);
                                lemmaMap.putIfAbsent(w, 1);
                            });
                }
            }
        }
        return lemmaMap;
    }
}


