package com.dians.deliverable.controller;

import com.dians.deliverable.exceptions.NoJobsException;
import com.dians.deliverable.models.*;
import com.dians.deliverable.payload.request.AddJobRequest;
import com.dians.deliverable.payload.response.JobResponse;
import com.dians.deliverable.payload.response.OptimizationDriverResponse;
import com.dians.deliverable.payload.response.OptimizationPreviewResponse;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.service.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/jobs")
public class JobController {

    private final CityService cityService;
    private final JobService jobService;
    private final UserService userService;
    private final OptimizationService optimizationService;
    private final NotificationService notificationService;

    public JobController(CityService cityService, JobService jobService, UserService userService, OptimizationService optimizationService, NotificationService notificationService) {
        this.cityService = cityService;
        this.jobService = jobService;
        this.userService = userService;
        this.optimizationService = optimizationService;
        this.notificationService = notificationService;
    }

    @GetMapping("/unassignedJobs")
    public ResponseEntity<?> getUnassignedJobs() {
        List<Job> jobs = jobService.getAllByStatus(JobStatus.NOT_ASSIGNED);
        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription())));

        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/assignedJobs")
    public ResponseEntity<?> getAssignedJobs() {
        List<Job> jobs = jobService.getAllByStatus(JobStatus.ASSIGNED);
        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(),
                job.getAssignedTo() == null ? "user" : job.getAssignedTo().getFirstName() + " " + job.getAssignedTo().getLastName())));

        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/completedJobs")
    public ResponseEntity<?> getCompletedJobs() {
        List<Job> jobs = jobService.getAllByStatus(JobStatus.COMPLETED);
        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(),
                job.getAssignedTo() == null ? "user" : job.getAssignedTo().getFirstName() + " " + job.getAssignedTo().getLastName())));

        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/addJob")
    public ResponseEntity<?> addJob(@RequestBody AddJobRequest addJobRequest) {

        if (addJobRequest.getCity().isBlank() || addJobRequest.getStreet().isBlank()
                || addJobRequest.getNumber().isBlank() || addJobRequest.getDescription().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Fill out all fields"));
        }

        String cityName = capitalize(addJobRequest.getCity());
        String streetName = capitalize(addJobRequest.getStreet());
        String number = addJobRequest.getNumber();
        String description = addJobRequest.getDescription();

        City city = cityService.findByName(cityName);
        if (city != null) {
            Street street = city.getStreets().stream()
                    .filter(s -> s.getName().equals(streetName))
                    .findFirst().orElse(null);

            if (street != null) {
                Address address = street.getAddresses().stream()
                        .filter(a -> a.getNumber().equals(number))
                        .findFirst().orElse(null);

                if (address != null) {
                    String fullAddress = cityName + " " + streetName + " " + number;
                    Job job = new Job(fullAddress, description, address.getLat(), address.getLon(), JobStatus.NOT_ASSIGNED);
                    if (!jobService.exists(fullAddress, description)) {
                        jobService.save(job);
                        return ResponseEntity
                                .ok(new MessageResponse("Job added successfully"));
                    }

                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Job already exists"));
                }
            }
        }

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Address doesn't exist"));
    }

    @PostMapping("/assignJobs")
    public ResponseEntity<?> assignJobs(@RequestBody List<Long> driverIds) {

        if(driverIds.size() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("No drivers selected"));
        }

        try {
            String response = optimizationService.getVroomResponse(driverIds);

            JSONObject json = new JSONObject(response);
            JSONArray routes = json.getJSONArray("routes");
            for(int i=0; i<routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                Long driverId = route.getLong("vehicle");
                AppUser driver = userService.getById(driverId);
                JSONArray steps = route.getJSONArray("steps");
                for(int j=1; j<steps.length() - 1; j++) {
                    JSONObject step = steps.getJSONObject(j);
                    Long jobId = step.getLong("id");
                    Job job = jobService.getById(jobId);
                    job.setAssignedTo(driver);
                    job.setStatus(JobStatus.ASSIGNED);
                    jobService.save(job);
                    driver.getCurrentJobs().add(job);
                }
                driver.setTotalJobs(driver.getCurrentJobs().size());
                userService.save(driver);
            }

            return ResponseEntity
                    .ok(new MessageResponse("Assigned jobs successfully"));

        } catch (NoJobsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All jobs have been assigned"));
        } catch (JSONException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All selected drivers already have assigned jobs"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Can't reach optimization service"));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<?> getPreview(@RequestBody List<Long> driverIds) {
        try {
            String vroomResponse = optimizationService.getVroomResponse(driverIds);
            System.out.println(vroomResponse);
            OptimizationPreviewResponse response = new OptimizationPreviewResponse();

            JSONObject json = new JSONObject(vroomResponse);
            int unassignedJobs = json.getJSONObject("summary").getInt("unassigned");
            int totalJobs = jobService.getAllByStatus(JobStatus.NOT_ASSIGNED).size();

            JSONArray routes = json.getJSONArray("routes");
            int totalDrivers = driverIds.size();
            int usedDrivers = routes.length();

            int maxTime = Integer.MIN_VALUE;
            double fuel = 0;
            for(int i=0; i<routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                int duration = route.getInt("duration");
                int service = route.getInt("service");
                fuel += 1f * duration * 7/9000;

                maxTime = Math.max(duration+service, maxTime);
                Long driverId = route.getLong("vehicle");
                AppUser driver = userService.getById(driverId);
                OptimizationDriverResponse mockDriver = new OptimizationDriverResponse();
                mockDriver.setFirstName(driver.getFirstName());
                mockDriver.setLastName(driver.getLastName());
                JSONArray steps = route.getJSONArray("steps");
                for(int j=1; j<steps.length() - 1; j++) {
                    JSONObject step = steps.getJSONObject(j);
                    Long jobId = step.getLong("id");
                    Job job = jobService.getById(jobId);
                    mockDriver.getJobs().add(job);
                }
                response.getDrivers().add(mockDriver);
            }

            int hours = maxTime / 3600;
            int minutes = (maxTime % 3600) / 60;
            int seconds = maxTime % 60;

            response.setAssignedJobs(String.format("%d/%d", totalJobs-unassignedJobs, totalJobs));
            response.setDriversUsed(String.format("%d/%d", usedDrivers, totalDrivers));
            response.setFuelCost(String.format("%.2fL", fuel));
            response.setTime(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            return ResponseEntity
                    .ok(response);

        } catch (NoJobsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All jobs have been assigned"));
        } catch (JSONException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All selected drivers already have assigned jobs"));
        } catch (PortUnreachableException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Can't reach optimization service"));
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        jobService.delete(id);

        return ResponseEntity
                .ok("");
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @DeleteMapping("/deleteAllNotifications")
    public void deleteAllNotifications() {
        notificationService.deleteAll();
    }

    private String capitalize(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
