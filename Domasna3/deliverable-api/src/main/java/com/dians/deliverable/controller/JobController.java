package com.dians.deliverable.controller;

import com.dians.deliverable.models.*;
import com.dians.deliverable.payload.request.AddJobRequest;
import com.dians.deliverable.payload.response.JobResponse;
import com.dians.deliverable.payload.vroom.VroomRequest;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.service.CityService;
import com.dians.deliverable.service.JobService;
import com.dians.deliverable.service.UserService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/jobs")
public class JobController {

    @Value("${vroomUrl}")
    private String vroomUrl;

    private final CityService cityService;
    private final JobService jobService;
    private final UserService userService;

    public JobController(CityService cityService, JobService jobService, UserService userService) {
        this.cityService = cityService;
        this.jobService = jobService;
        this.userService = userService;
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
        String description = addJobRequest.getDescription();
        int number;
        try {
            number = Integer.parseInt(addJobRequest.getNumber());
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid number format"));
        }

        City city = cityService.findByName(cityName);
        if (city != null) {
            Street street = city.getStreets().stream()
                    .filter(s -> s.getName().equals(streetName))
                    .findFirst().orElse(null);

            if (street != null) {
                Address address = street.getAddresses().stream()
                        .filter(a -> a.getNumber() == number)
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

        VroomRequest vroomRequest = new VroomRequest();
        double startLon = 21.4443826;
        double startLat = 41.994568;

        List<Job> unassignedJobs = jobService.getAllByStatus(JobStatus.NOT_ASSIGNED);

        if(unassignedJobs.size() == 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All jobs have been assigned"));
        }

        unassignedJobs.forEach(job -> vroomRequest.addJob(job.getId(), job.getLon(), job.getLat()));

        int capacity = (int) Math.ceil(1f * unassignedJobs.size() / driverIds.size());
        List<AppUser> alreadyHaveJobs = new ArrayList<>();
        driverIds.forEach(driverId -> {
            AppUser driver = userService.getById(driverId);
            if(driver.getCurrentJobs().size() == 0) {
                vroomRequest.addDriver(driverId, startLon, startLat, capacity);
            } else {
                alreadyHaveJobs.add(driver);
            }
        });

        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpPost post = new HttpPost(vroomUrl);
            StringEntity postingString = new StringEntity(new JSONObject(vroomRequest).toString());
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(post);

            InputStream in = response.getEntity().getContent();
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (in, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            System.out.println(textBuilder.toString());
            JSONObject json = new JSONObject(textBuilder.toString());
            int unassigned = json.getJSONObject("summary").getInt("unassigned");

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
                userService.save(driver);
            }

            return ResponseEntity
                    .ok(new MessageResponse(String.format(" Assigned %d of %d jobs. %s", unassignedJobs.size() - unassigned, unassignedJobs.size(),
                            alreadyHaveJobs.size() == 0 ? "" :
                                    "Drivers: " + alreadyHaveJobs.stream()
                                            .map(driver -> driver.getFirstName() + " " + driver.getLastName())
                                            .collect(Collectors.joining(", ")) + " already have jobs.")));
        } catch (JSONException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("All selected drivers already have assigned jobs"));
        }
        catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        jobService.delete(id);

        return ResponseEntity
                .ok("");
    }

    private String capitalize(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
