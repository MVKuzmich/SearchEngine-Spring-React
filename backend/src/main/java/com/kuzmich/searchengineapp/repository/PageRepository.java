package com.kuzmich.searchengineapp.repository;

import com.kuzmich.searchengineapp.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    int countPageBySiteId(int siteId);

    @Query("select p from Page p where p.path = :path and p.site.id = :siteId")
    Optional<Page> findPageByPath(String path, int siteId);
}
