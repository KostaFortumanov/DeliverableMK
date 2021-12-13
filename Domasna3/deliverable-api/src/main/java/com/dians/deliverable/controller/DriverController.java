package com.dians.deliverable.controller;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.payload.response.SelectDriverResponse;
import com.dians.deliverable.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/drivers")
public class DriverController {

    private final UserService userService;

    public DriverController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/selectDrivers")
    public ResponseEntity<?> getSelectDrivers() {
        List<AppUser> drivers = userService.getAllDrivers();

        List<SelectDriverResponse> response = new ArrayList<>();

        drivers.forEach(driver ->
                response.add(new SelectDriverResponse(driver.getId(), driver.getFirstName() + " " + driver.getLastName())));



        return ResponseEntity
                .ok(response);
    }
}
