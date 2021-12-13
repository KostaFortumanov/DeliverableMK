package com.dians.deliverable.controller;

import com.dians.deliverable.models.*;
import com.dians.deliverable.payload.request.AddJobRequest;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.service.CityService;
import com.dians.deliverable.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/addJob")
    public ResponseEntity<?> addJob(@RequestBody AddJobRequest addJobRequest) {

        if(addJobRequest.getCity().isBlank() || addJobRequest.getStreet().isBlank()
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
        if(city != null) {
            Street street = city.getStreets().stream()
                    .filter(s -> s.getName().equals(streetName))
                    .findFirst().orElse(null);

            if(street != null) {
                Address address = street.getAddresses().stream()
                        .filter(a -> a.getNumber() == number)
                        .findFirst().orElse(null);

                if(address != null) {
                    String fullAddress = cityName + " " + streetName + " " + number;
                    Job job = new Job(fullAddress, description, address.getLat(), address.getLon(), JobStatus.NOT_ASSIGNED);
                    if(!jobService.exists(fullAddress, description)) {
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

    private String capitalize(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
