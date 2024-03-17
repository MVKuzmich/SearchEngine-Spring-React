package com.kuzmich.searchengineapp;

import com.kuzmich.searchengineapp.action.Lemmatizator;
import lombok.SneakyThrows;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LemmatizatorTest {

    private Lemmatizator lemmatizator;

    private LuceneMorphology luceneMorphology;

    @SneakyThrows
    @BeforeEach
    void prepare() {
        luceneMorphology = new RussianLuceneMorphology();
        lemmatizator = new Lemmatizator(luceneMorphology);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"конюшня бывает из дерева и покрашена краской", "да будет свет", "мы вместе"})
    void countLemmasAfterLemmatizator(String text) {
        Map<String, Integer> result = lemmatizator.getLemmaList(text);
        assertThat(result.keySet()).isNotEmpty();


    }

}
