package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.Statistics;
import com.dians.deliverable.auth_service.payload.request.EditDriverRequest;
import com.dians.deliverable.auth_service.payload.response.DriverInfoResponse;
import com.dians.deliverable.auth_service.payload.response.MessageResponse;
import com.dians.deliverable.auth_service.payload.response.SelectDriverResponse;
import com.dians.deliverable.auth_service.service.StatisticsService;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class DriverController {

    private final UserService userService;
    private final StatisticsService statisticsService;

    public DriverController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/allDriverInfo")
    public ResponseEntity<?> getAllDriverInfo() {

        List<AppUser> drivers = userService.getAllDrivers();
        List<DriverInfoResponse> response = new ArrayList<>();

        drivers.forEach(driver -> response.add(new DriverInfoResponse(driver.getId(),
                driver.getFirstName() + " " + driver.getLastName(),
                driver.getEmail(),
                driver.getPhoneNumber()
        )));

        return ResponseEntity
                .ok(response.stream().sorted(Comparator.comparing(DriverInfoResponse::getId)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {

        AppUser user = userService.getById(id);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:9092/api/jobs/removeJobAssignment/" + id, Void.class);

        List<Statistics> statistics = statisticsService.findAllByDriver(id);
        statistics.forEach(statistic -> statistic.setAppUser(null));

        userService.deleteUser(user);

        return ResponseEntity
                .ok(new MessageResponse("Driver removed successfully"));
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editDriver(@RequestBody EditDriverRequest editDriverRequest) {

        AppUser user = userService.getById(editDriverRequest.getId());

        if(!user.getEmail().equals(editDriverRequest.getNewEmail().toLowerCase().trim()) && userService.existsByEmail(editDriverRequest.getNewEmail().toLowerCase().trim())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email already exists"));
        }

        user.setEmail(editDriverRequest.getNewEmail().toLowerCase().trim());
        user.setPhoneNumber(editDriverRequest.getNewPhonenumber());
        userService.save(user);

        return ResponseEntity
                .ok(new MessageResponse("Changes saved"));
    }

    @GetMapping("/selectDrivers")
    public ResponseEntity<?> getSelectDrivers() {
        List<AppUser> drivers = userService.getAllDrivers();

        List<SelectDriverResponse> response = new ArrayList<>();

        drivers.forEach(driver ->
                response.add(new SelectDriverResponse(driver.getId(),
                        driver.getFirstName() + " " + driver.getLastName(),
                        driver.getTotalJobs() == 0)));

        return ResponseEntity
                .ok(response.stream().sorted(Comparator.comparing(SelectDriverResponse::isAvailable).reversed()));
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
