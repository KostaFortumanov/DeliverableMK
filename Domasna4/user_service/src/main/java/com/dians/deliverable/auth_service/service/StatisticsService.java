package com.dians.deliverable.auth_service.service;

import com.dians.deliverable.auth_service.models.Statistics;
import com.dians.deliverable.auth_service.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository repository;

    public StatisticsService(StatisticsRepository repository) {
        this.repository = repository;
    }

    public void save(Statistics statistics) {
        repository.save(statistics);
    }

    public List<Statistics> findAll() {
        return repository.findAll();
    }

    public List<Statistics> findAllByDriver(Long userId) {
        return repository.findAllByAppUser(userId);
    }

    public List<Statistics> findInMonth(int month) {
        LocalDate[] dates = getDates(month);
        return repository.findAllByDateBetween(dates[0], dates[1]);
    }

    public List<Statistics> findByDriverInMonth(Long userId, int month) {
        LocalDate[] dates = getDates(month);
        return repository.findAllByAppUserAndDateBetween(userId, dates[0], dates[1]);
    }

    private LocalDate[] getDates(int month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.parse(String.format("01.%02d.2022", month), formatter);
        LocalDate endDate = LocalDate.parse(String.format("31.%02d.2022", month), formatter);
        return new LocalDate[]{startDate, endDate};
    }
}
