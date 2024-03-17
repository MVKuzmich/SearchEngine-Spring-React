package com.kuzmich.searchengineapp;

import com.kuzmich.searchengineapp.action.Lemmatizator;
import com.kuzmich.searchengineapp.action.SearchPageExecution;
import com.kuzmich.searchengineapp.dto.searchDto.SearchPageData;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import lombok.SneakyThrows;
import org.apache.lucene.morphology.LuceneMorphology;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class SearchPageExecutionTest {

    @InjectMocks
    private SearchPageExecution searchPageExecution;
    @Mock
    private LemmaRepository lemmaRepository;
    @Mock
    private SiteRepository siteRepository;

    @Mock
    private LuceneMorphology luceneMorphology;


    @BeforeEach
    void prepare() {
        searchPageExecution = new SearchPageExecution(lemmaRepository, siteRepository, luceneMorphology);
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForCreateRegexTest")
    void createRegexTest(List<String> wordList, String expected) {
        try {
            Method method = SearchPageExecution.class.getDeclaredMethod("createRegex", List.class);
            method.setAccessible(true);
            assertThat(method.invoke(searchPageExecution, wordList)).isEqualTo("(?i)\\b(л|д)([А-яЁё]+)(-\1)?");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    static Stream<Arguments> getArgumentsForCreateRegexTest() {
        return Stream.of(
                Arguments.of(List.of("лучший", "друг"), "(?i)\\b(л|д)([А-яЁё]+)(-\1)?"),
                Arguments.of(List.of("я", "лучший", "друг"), "(?i)\\b(л|д)([А-яЁё]+)(-\1)?")
        );
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/findSentenceTestData.csv", delimiter = '|', numLinesToSkip = 2)
    void getSentenceTest(String word, String text, String expected) {
        try {
            Method method = SearchPageExecution.class.getDeclaredMethod(
                    "getSentenceFromTextElement", String.class, String.class);
            method.setAccessible(true);
            assertThat(method.invoke(searchPageExecution, word, text)).isEqualTo(expected);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvSource(value = "10.0f:0.8f:0.2f:0.9f", delimiter = ':')
    void updateRelevanceTest(float maxRank, double expected1, double expected2, double expected3) {
        List<SearchPageData> list = List.of(
                new SearchPageData("http://ya.ru", "Я", "/about", "О нас", "Что-то о нас", 8.0),
                new SearchPageData("http://ya.ru", "Я", "/news", "Новости", "Свежие Новости", 2.0),
                new SearchPageData("http://ya.ru", "Я", "/daynews", "Новости дня", "Свежие Новости Дня", 9.0)

        );
        List<SearchPageData> expected = List.of(
                new SearchPageData("http://ya.ru", "Я", "/about", "О нас", "Что-то о нас", expected1),
                new SearchPageData("http://ya.ru", "Я", "/news", "Новости", "Свежие Новости", expected2),
                new SearchPageData("http://ya.ru", "Я", "/daynews", "Новости дня", "Свежие Новости Дня", expected3)
        );

        try {
            Method method = SearchPageExecution.class.getDeclaredMethod("updateSearchPageDataRelevance", float.class, List.class);
            method.setAccessible(true);
            assertThat(method.invoke(searchPageExecution, maxRank, list)).isEqualTo(expected);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
