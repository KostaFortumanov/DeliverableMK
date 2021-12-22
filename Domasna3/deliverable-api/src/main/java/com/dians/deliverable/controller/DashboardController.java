package com.dians.deliverable.controller;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Statistics;
import com.dians.deliverable.payload.response.DayResponse;
import com.dians.deliverable.payload.response.TotalDashboardResponse;
import com.dians.deliverable.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StatisticsService statisticsService;

    public DashboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/manager/{month}")
    public ResponseEntity<?> getManagerDashboard(@PathVariable int month) {

        List<Statistics> statistics = statisticsService.findInMonth(month);
        Map<Integer, DayResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            int day = statistic.getDate().getDayOfMonth();
            map.computeIfAbsent(day, value -> new DayResponse());
            double distance = map.get(day).getDistance();
            double fuel = map.get(day).getFuel();
            double numJobs = map.get(day).getNumJobs();

            map.get(day).setDistance(distance + statistic.getDistance()/1000);
            map.get(day).setFuel(fuel + statistic.getFuel());
            map.get(day).setNumJobs(numJobs + 1);
        }

        for(int i=1; i<=31; i++) {
            map.computeIfAbsent(i, value -> new DayResponse(0, 0,0));
        }

        return ResponseEntity
                .ok(map.values());
    }

    @GetMapping("/driver/{month}")
    public ResponseEntity<?> getDriverDashboard(@PathVariable Integer month) {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Statistics> statistics = statisticsService.findByDriverInMonth(user, month);

        Map<Integer, DayResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            int day = statistic.getDate().getDayOfMonth();
            map.computeIfAbsent(day, value -> new DayResponse());
            double distance = map.get(day).getDistance();
            double fuel = map.get(day).getFuel();
            double numJobs = map.get(day).getNumJobs();

            map.get(day).setDistance(distance + statistic.getDistance()/1000);
            map.get(day).setFuel(fuel + statistic.getFuel());
            map.get(day).setNumJobs(numJobs + 1);
        }

        for(int i=1; i<=31; i++) {
            map.computeIfAbsent(i, value -> new DayResponse(0, 0,0));
        }

        return ResponseEntity
                .ok(map.values());
    }

    @GetMapping("/driverTotal")
    public ResponseEntity<?> getDriverTotal() {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Statistics> statistics = statisticsService.findAllByDriver(user);

        Map<String, TotalDashboardResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            String name = statistic.getUser().getFirstName() + " " + statistic.getUser().getLastName();
            map.computeIfAbsent(name, value -> new TotalDashboardResponse(name));
            double distance = map.get(name).getDistance();
            double fuel = map.get(name).getFuel();
            double numJobs = map.get(name).getNumJobs();

            map.get(name).setDistance(distance + statistic.getDistance()/1000);
            map.get(name).setFuel(fuel + statistic.getFuel());
            map.get(name).setNumJobs(numJobs + 1);
        }

        return ResponseEntity
                .ok(map.values());
    }

    @GetMapping("/managerTotal")
    public ResponseEntity<?> getManagerTotal() {
        List<Statistics> statistics = statisticsService.findAll();

        Map<String, TotalDashboardResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            String name = statistic.getUser() == null ? "user" : statistic.getUser().getFirstName() + " " + statistic.getUser().getLastName();
            map.computeIfAbsent(name, value -> new TotalDashboardResponse(name));
            double distance = map.get(name).getDistance();
            double fuel = map.get(name).getFuel();
            double numJobs = map.get(name).getNumJobs();

            map.get(name).setDistance(distance + statistic.getDistance()/1000);
            map.get(name).setFuel(fuel + statistic.getFuel());
            map.get(name).setNumJobs(numJobs + 1);
        }

        return ResponseEntity
                .ok(map.values());
    }
}
