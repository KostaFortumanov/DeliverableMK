package com.dians.deliverable.service;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Statistics;
import com.dians.deliverable.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public List<Statistics> findAllByDriver(AppUser user) {
        return repository.findAllByUser(user);
    }

    public List<Statistics> findInMonth(int month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.parse(String.format("01.%02d.2021", month), formatter);
        LocalDate endDate = LocalDate.parse(String.format("31.%02d.2021", month), formatter);
        return repository.findAllByDateBetween(startDate, endDate);
    }

    public List<Statistics> findByDriverInMonth(AppUser user, int month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.parse(String.format("01.%02d.2021", month), formatter);
        LocalDate endDate = LocalDate.parse(String.format("31.%02d.2021", month), formatter);
        return repository.findAllByUserAndDateBetween(user, startDate, endDate);
    }
}
