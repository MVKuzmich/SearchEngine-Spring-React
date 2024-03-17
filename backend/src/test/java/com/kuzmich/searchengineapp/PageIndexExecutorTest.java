package com.kuzmich.searchengineapp;

import com.kuzmich.searchengineapp.action.PageIndexExecutor;
import com.kuzmich.searchengineapp.entity.Lemma;
import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class PageIndexExecutorTest {

    @InjectMocks
    PageIndexExecutor pageIndexExecutor;
    @Mock
    LemmaRepository lemmaRepository;


    @Test
    void saveLemmaTest() {
        try {
        Site site = new Site(Status.INDEXING, System.currentTimeMillis(), "", "http://ya.ru", "Яндекс");
        site.setId(1);
        Lemma lemma = new Lemma("Добро", 1, site);
            Lemma saveLemma = new Lemma("Добро", 1, site);
        saveLemma.setId(1);
        Map<String, Integer> lemmaItem = Map.of("Добро", 5);
//        doReturn(Optional.empty())
//                .when(lemmaRepository).findLemmaObjectByLemmaNameAndSiteId(lemmaItem.keySet().stream().findFirst().get(), site.getId());
//        doReturn(saveLemma)
//                .when(lemmaRepository).saveAndFlush(lemma);

            Method method = pageIndexExecutor.getClass().getMethod("saveLemma", Map.Entry.class, Site.class);
            method.setAccessible(true);
            assertThat(method.invoke(pageIndexExecutor, lemmaItem.entrySet().stream().findFirst().get(), site))
            .isEqualTo(saveLemma);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
