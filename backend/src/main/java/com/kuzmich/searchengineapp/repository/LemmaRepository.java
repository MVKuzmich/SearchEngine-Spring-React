package com.kuzmich.searchengineapp.repository;

import com.kuzmich.searchengineapp.entity.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    @Query("select l from Lemma l where l.lemma = :lemmaName and l.site.id = :siteId")
    Optional<Lemma> findLemmaObjectByLemmaNameAndSiteId(String lemmaName, int siteId);

    @Query("select l from Lemma l where l.lemma = :lemmaName")
    Optional<List<Lemma>> findLemmaObjectByLemmaName(String lemmaName);

    int countLemmaBySiteId(int siteId);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("update Lemma l set l.frequency = (l.frequency - 1) where l.id = :id")
    void minusLemmaFrequencyById(int id);
}
