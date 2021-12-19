package com.dians.deliverable.repository;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findAllByUser(AppUser user);
    List<Statistics> findAllByDateBetween(LocalDate date1, LocalDate date2);
    List<Statistics> findAllByUserAndDateBetween(AppUser user, LocalDate date1, LocalDate date2);
}
