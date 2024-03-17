package com.kuzmich.searchengineapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lemma", indexes = @jakarta.persistence.Index(columnList = "lemma, site_id", unique = true))
public class Lemma extends BaseEntity {

    @NotNull
    private String lemma;
    @NotNull
    private int frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    @NotNull
    private Site site;

    @OneToMany(mappedBy = "lemma", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.kuzmich.searchengineapp.entity.Index> indexList = new ArrayList<>();

    public Lemma(String lemma, int frequency, Site site) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.site = site;
    }

    @Override
    public String toString() {
        return lemma;
    }



}
