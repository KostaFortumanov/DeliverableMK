package com.dians.deliverable.auth_service.repository;

import com.dians.deliverable.auth_service.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findAllByAppUser(Long userId);
    List<Statistics> findAllByDateBetween(LocalDate date1, LocalDate date2);
    List<Statistics> findAllByAppUserAndDateBetween(Long userId, LocalDate date1, LocalDate date2);
}