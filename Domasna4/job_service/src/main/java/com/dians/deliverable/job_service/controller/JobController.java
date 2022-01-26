package com.dians.deliverable.job_service.controller;

import com.dians.deliverable.job_service.models.*;
import com.dians.deliverable.job_service.payload.request.AddJobRequest;
import com.dians.deliverable.job_service.payload.response.JobResponse;
import com.dians.deliverable.job_service.payload.response.MessageResponse;
import com.dians.deliverable.job_service.service.CityService;
import com.dians.deliverable.job_service.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/jobs")
public class JobController {

    private final CityService cityService;
    private final JobService jobService;

    public JobController(CityService cityService, JobService jobService) {
        this.cityService = cityService;
        this.jobService = jobService;
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

    private String getUserFullName(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(URI.create("http://localhost:9091/api/user/" + id + "/name"), String.class);
    }

    private List<JobResponse> getAllJobsByStatus(JobStatus status) {
        List<Job> jobs = jobService.getAllByStatus(status);
        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(),
                job.getAssignedTo() == null ? "user" : getUserFullName(job.getAssignedTo()))));

        return response;
    }

    private List<JobResponse> getAllJobsByDriverAndStatus(String id, JobStatus status) {
        Long userId = Long.parseLong(id);
        List<Job> jobs = jobService.getByDriverAndStatus(userId, status);

        List<JobResponse> response = new ArrayList<>();

        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription(), getUserFullName(userId))));

        return response;
    }
}

