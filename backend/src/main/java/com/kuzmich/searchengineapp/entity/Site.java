package com.kuzmich.searchengineapp.entity;


import java.util.ArrayList;
import java.util.List;

import com.kuzmich.searchengineapp.utils.HashGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


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
    @NotBlank(message = "The url can't be empty")
    @Column(unique = true)
    private String url;
    @NotNull
    private String name;
    @NotNull
    private String hash;


    public Site(Status status, long statusTime, String lastError, String url, String name) {
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
        this.url = url;
        this.name = name;
        this.hash = HashGenerator.generateHash(url); 
    }

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Page> pageList = new ArrayList<>();

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lemma> lemmaList = new ArrayList<>();

    
}
