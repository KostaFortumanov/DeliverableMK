package com.dians.deliverable.controller;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Job;
import com.dians.deliverable.payload.request.UpdateCurrentPathRequest;
import com.dians.deliverable.payload.response.JobResponse;
import com.dians.deliverable.service.RouteFinderService;
import com.dians.deliverable.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/map")
public class MapController {

    private final RouteFinderService routeFinderService;
    private final UserService userService;

    public MapController(RouteFinderService routeFinderService, UserService userService) {
        this.routeFinderService = routeFinderService;
        this.userService = userService;
    }

    @GetMapping("/allPaths")
    public ResponseEntity<?> getAllPaths() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Job> jobs = userService.getById(user.getId()).getCurrentJobs();
        double startLon = 21.4443826;
        double startLat = 41.994568;

        return ResponseEntity
                .ok(routeFinderService.getPaths(startLon, startLat, jobs));

    }

    @GetMapping("/currentJobs")
    public ResponseEntity<?> getCurrentJobs() {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Job> jobs = userService.getById(user.getId()).getCurrentJobs();

        List<JobResponse> response = new ArrayList<>();
        jobs.forEach(job -> response.add(new JobResponse(job.getId(), job.getAddress(), job.getDescription())));

        return ResponseEntity
                .ok(response);
    }

    @PostMapping("/updateCurrentPath")
    public ResponseEntity<?> updateCurrentPath(@RequestBody UpdateCurrentPathRequest request) {

        System.out.println( request.getLon() + " "
                 + request.getLat()
                 + " " + request.getDestinationLon()
                 + " " + request.getDestinationLat());

        return ResponseEntity
                .ok(routeFinderService.getPath(
                        request.getLon(),
                        request.getLat(),
                        request.getDestinationLon(),
                        request.getDestinationLat()));
    }
}
