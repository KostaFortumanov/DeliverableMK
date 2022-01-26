package com.dians.deliverable.job_service.controller;

import com.dians.deliverable.job_service.exceptions.NoJobsException;
import com.dians.deliverable.job_service.models.*;
import com.dians.deliverable.job_service.payload.request.AddJobRequest;
import com.dians.deliverable.job_service.payload.response.JobResponse;
import com.dians.deliverable.job_service.payload.response.MessageResponse;
import com.dians.deliverable.job_service.payload.response.OptimizationDriverResponse;
import com.dians.deliverable.job_service.payload.response.OptimizationPreviewResponse;
import com.dians.deliverable.job_service.service.CityService;
import com.dians.deliverable.job_service.service.InternalService;
import com.dians.deliverable.job_service.service.JobService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final CityService cityService;
    private final JobService jobService;
    private final InternalService internalService;

    public JobController(CityService cityService, JobService jobService, InternalService internalService) {
        this.cityService = cityService;
        this.jobService = jobService;
        this.internalService = internalService;
    }

    @GetMapping("/unassignedJobs")
    public ResponseEntity<?> getUnassignedJobs() {
        return ResponseEntity
                .ok(getAllJobsByStatus(JobStatus.NOT_ASSIGNED));
    }

    @GetMapping("/assignedJobs")
    public ResponseEntity<?> getAssignedJobs() {
        return ResponseEntity
                .ok(getAllJobsByStatus(JobStatus.ASSIGNED));
    }

    @GetMapping("/completedJobs")
    public ResponseEntity<?> getCompletedJobs() {
        return ResponseEntity
                .ok(getAllJobsByStatus(JobStatus.COMPLETED));
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        jobService.delete(id);

        return ResponseEntity
                .ok("");
    }

    @PostMapping("/assignJobs")
    public ResponseEntity<?> assignJobs(@RequestBody List<Long> driverIds) {

        if(driverIds.size() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("No drivers selected"));
        }

        if(jobService.getAllByStatus(JobStatus.NOT_ASSIGNED).size() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All jobs have been assigned"));
        }

        try {
            String response = internalService.getVroomResponse(driverIds);

            JSONObject json = new JSONObject(response);
            JSONArray routes = json.getJSONArray("routes");
            for(int i=0; i<routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                Long driverId = route.getLong("vehicle");
                JSONArray steps = route.getJSONArray("steps");
                for(int j=1; j<steps.length() - 1; j++) {
                    JSONObject step = steps.getJSONObject(j);
                    Long jobId = step.getLong("id");
                    Job job = jobService.getById(jobId);
                    job.setAssignedTo(driverId);
                    job.setStatus(JobStatus.ASSIGNED);
                    jobService.save(job);
                }
                internalService.setTotalJobs(driverId, steps.length()-2);
            }

            return ResponseEntity
                    .ok(new MessageResponse("Assigned jobs successfully"));

        } catch (JSONException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All selected drivers already have assigned jobs"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Can't reach optimization service"));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<?> getPreview(@RequestBody List<Long> driverIds) {

        if(jobService.getAllByStatus(JobStatus.NOT_ASSIGNED).size() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All jobs have been assigned"));
        }

        try {
            String vroomResponse = internalService.getVroomResponse(driverIds);
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
                String[] name = internalService.getUserFullName(driverId).split(" ");
                OptimizationDriverResponse mockDriver = new OptimizationDriverResponse();
                mockDriver.setFirstName(name[0]);
                mockDriver.setLastName(name[1]);
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

        } catch (JSONException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All selected drivers already have assigned jobs"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Can't reach optimization service"));
        }

    }

    @GetMapping("/myAssigned")
    public ResponseEntity<?> getMyAssignedJobs(@RequestHeader String userId) {
        return ResponseEntity
                .ok(getAllJobsByDriverAndStatus(userId, JobStatus.ASSIGNED));
    }

    @GetMapping("/myCompleted")
    public ResponseEntity<?> getMyCompletedJobs(@RequestHeader String userId) {
        return ResponseEntity
                .ok(getAllJobsByDriverAndStatus(userId, JobStatus.COMPLETED));
    }

    private String capitalize(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private List<JobResponse> getAllJobsByStatus(JobStatus status) {
        List<Job> jobs = jobService.getAllByStatus(status);
        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(),
                job.getAssignedTo() == null ? "user" : internalService.getUserFullName(job.getAssignedTo()))));

        return response;
    }

    private List<JobResponse> getAllJobsByDriverAndStatus(String id, JobStatus status) {
        Long userId = Long.parseLong(id);
        List<Job> jobs = jobService.getByDriverAndStatus(userId, status);

        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(), internalService.getUserFullName(userId))));

        return response;
    }
}

