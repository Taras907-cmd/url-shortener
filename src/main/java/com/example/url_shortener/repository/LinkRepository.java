package com.example.url_shortener.repository;

import com.example.url_shortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<Link> findByUserId(Long userId);

    List<Link> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);

    @Modifying
    @Query("update Link l set l.clickCount = l.clickCount + 1 where l.id = :id")
    int incrementClickCount(@Param("id") Long id);



}