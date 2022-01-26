package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.Statistics;
import com.dians.deliverable.auth_service.payload.response.DayResponse;
import com.dians.deliverable.auth_service.payload.response.TotalDashboardResponse;
import com.dians.deliverable.auth_service.service.StatisticsService;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StatisticsService statisticsService;
    private final UserService userService;

    public DashboardController(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    @GetMapping("/manager/{month}")
    public ResponseEntity<?> getManagerDashboard(@PathVariable Integer month) {

        List<Statistics> statistics = statisticsService.findInMonth(month);
        return ResponseEntity
                .ok(getDashboard(statistics));
    }

    @GetMapping("/driver/{month}")
    public ResponseEntity<?> getDriverDashboard(@RequestHeader String userId, @PathVariable Integer month) {

        Long id = Long.parseLong(userId);
        List<Statistics> statistics = statisticsService.findByDriverInMonth(id, month);
        return ResponseEntity
                .ok(getDashboard(statistics));
    }

    @GetMapping("/driverTotal")
    public ResponseEntity<?> getDriverTotal(@RequestHeader String userId) {
        Long id = Long.parseLong(userId);
        List<Statistics> statistics = statisticsService.findAllByDriver(id);
        return ResponseEntity
                .ok(getTotal(statistics));
    }

    @GetMapping("/managerTotal")
    public ResponseEntity<?> getManagerTotal() {
        List<Statistics> statistics = statisticsService.findAll();
        return ResponseEntity
                .ok(getTotal(statistics));
    }

    private Collection<DayResponse> getDashboard(List<Statistics> statistics) {
        Map<Integer, DayResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            int day = statistic.getDate().getDayOfMonth();
            map.computeIfAbsent(day, value -> new DayResponse());
            setValues(map, day, statistic);
        }

        for(int i=1; i<=31; i++) {
            map.computeIfAbsent(i, value -> new DayResponse(0, 0,0));
        }

        return map.values();
    }

    private Collection<TotalDashboardResponse> getTotal(List<Statistics> statistics) {
        Map<String, TotalDashboardResponse> map = new HashMap<>();
        for(Statistics statistic: statistics) {
            String name = statistic.getAppUser() == null ? "user" : getUserFullName(statistic.getAppUser());
            map.computeIfAbsent(name, value -> new TotalDashboardResponse(name));
            setValues(map, name, statistic);
        }

        return map.values();
    }

    private <K, V extends DayResponse> void setValues(Map<K, V> map, K key, Statistics statistic) {
        double distance = map.get(key).getDistance();
        double fuel = map.get(key).getFuel();
        double numJobs = map.get(key).getNumJobs();
        map.get(key).setDistance(distance + statistic.getDistance()/1000);
        map.get(key).setFuel(fuel + statistic.getFuel());
        map.get(key).setNumJobs(numJobs + 1);
    }

    private String getUserFullName(Long id) {
        AppUser user = userService.getById(id);
        return user.getFirstName() + " " + user.getLastName();
    }
}
