package com.kuzmich.searchengineapp.repository;

import com.kuzmich.searchengineapp.entity.Site;
import com.kuzmich.searchengineapp.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface SiteRepository extends JpaRepository<Site, Integer> {

    @Query("select s from Site s where s.url = :url")
    Optional<Site> findSiteByUrl(String url);

    @Modifying(flushAutomatically = true)
    void removeSiteById(Integer id);

    @Modifying(flushAutomatically = true)
    @Query("update Site s set s.status = :status where s.id = :id")
    void updateSiteStatus(Status status, Integer id);

    @Modifying(flushAutomatically = true)
    @Query("update Site s set s.statusTime = :statusTime where s.id = :id")
    void updateSiteStatusTime(long statusTime, Integer id);

    @Modifying(flushAutomatically = true)
    @Query("update Site s set s.status = :status, s.lastError = :error where s.id = :id")
    void updateSiteStatusAndError(Status status, String error, Integer id);

}
