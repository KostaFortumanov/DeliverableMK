package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.Statistics;
import com.dians.deliverable.auth_service.service.StatisticsService;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// methods only used for communication between microservices
@RestController
@RequestMapping("/api/user")
public class InternalController {

    private final UserService userService;
    private final StatisticsService statisticsService;

    public InternalController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDriver(@PathVariable Long id) {
        AppUser user = userService.getById(id);
        return ResponseEntity
                .ok(user);
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getDriverFullName(@PathVariable Long id) {
        AppUser user = userService.getById(id);
        return ResponseEntity
                .ok(user.getFirstName() + " " + user.getLastName());
    }

    @PostMapping("/saveStatistics")
    public void saveStatistics(@RequestBody Statistics statistics) {
        statisticsService.save(statistics);
    }

    @PostMapping("/setTotalJobs/{userId}")
    public void setTotalJobs(@PathVariable Long userId, @RequestBody Integer jobs) {
        AppUser user = userService.getById(userId);
        user.setTotalJobs(jobs);
        userService.save(user);
    }
}
