package com.dians.deliverable.controller;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Job;
import com.dians.deliverable.payload.request.EditDriverRequest;
import com.dians.deliverable.payload.response.DriverInfoResponse;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.payload.response.SelectDriverResponse;
import com.dians.deliverable.service.JobService;
import com.dians.deliverable.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/drivers")
public class DriverController {

    private final UserService userService;
    private final JobService jobService;

    public DriverController(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    @GetMapping("/allDriverInfo")
    public ResponseEntity<?> getAllDriverInfo() {

        List<AppUser> drivers = userService.getAllDrivers();
        List<DriverInfoResponse> response = new ArrayList<>();

        drivers.forEach(driver -> response.add(new DriverInfoResponse(driver.getId(),
                        driver.getFirstName() + driver.getLastName(),
                                driver.getEmail(),
                                driver.getPhoneNumber()
                )));

        return ResponseEntity
                .ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {

        AppUser user = userService.getById(id);
        List<Job> jobs = jobService.getByDriver(user);
        jobs.forEach(job -> job.setAssignedTo(null));

        userService.deleteUser(user);

        return ResponseEntity
                .ok(new MessageResponse("Driver removed successfully"));
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editDriver(@RequestBody EditDriverRequest editDriverRequest) {
        AppUser user = userService.getById(editDriverRequest.getId());

        user.setEmail(editDriverRequest.getNewEmail());
        user.setPhoneNumber(editDriverRequest.getNewPhonenumber());
        userService.save(user);

        return ResponseEntity
                .ok("");
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
