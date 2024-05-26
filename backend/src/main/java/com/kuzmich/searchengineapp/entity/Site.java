package com.kuzmich.searchengineapp.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Site extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    @Column(name = "status_time")
    @NotNull
    private long statusTime;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @NotNull
    private String url;
    @NotNull
    private String name;


    public Site(Status status, long statusTime, String lastError, String url, String name) {
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
        this.url = url;
        this.name = name;
    }

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Page> pageList = new ArrayList<>();

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lemma> lemmaList = new ArrayList<>();

    
}
