package com.dians.deliverable.controller;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Job;
import com.dians.deliverable.models.JobStatus;
import com.dians.deliverable.models.Statistics;
import com.dians.deliverable.payload.request.UpdateCurrentPathRequest;
import com.dians.deliverable.payload.response.JobResponse;
import com.dians.deliverable.service.JobService;
import com.dians.deliverable.service.RouteFinderService;
import com.dians.deliverable.service.StatisticsService;
import com.dians.deliverable.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "${frontUrl}", maxAge = 3600)
@RequestMapping("/api/map")
public class MapController {

    private final RouteFinderService routeFinderService;
    private final UserService userService;
    private final JobService jobService;
    private final StatisticsService statisticsService;

    public MapController(RouteFinderService routeFinderService, UserService userService, JobService jobService, StatisticsService statisticsService) {
        this.routeFinderService = routeFinderService;
        this.userService = userService;
        this.jobService = jobService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/allPaths")
    public ResponseEntity<?> getAllPaths() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Job> jobs = userService.getById(user.getId()).getCurrentJobs();

        if(jobs.size() == 0) {
            return ResponseEntity
                    .badRequest().body("");
        }

        double startLon = 21.4443826;
        double startLat = 41.994568;

        return ResponseEntity
                .ok(routeFinderService.getPaths(startLon, startLat, jobs));

    }

    @GetMapping("/finishJob")
    public ResponseEntity<?> finishJob() {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getById(user.getId());
        Job job = user.getCurrentJobs().remove(0);
        job.setStatus(JobStatus.COMPLETED);

        double distance = job.getDistance();
        double fuel = distance/100000 * 8;

        statisticsService.save(new Statistics(fuel, distance, LocalDate.now(), user));
        userService.save(user);
        jobService.save(job);
        return ResponseEntity.ok("");
    }

    @GetMapping("/currentJobs")
    public ResponseEntity<?> getCurrentJobs() {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getById(user.getId());
        List<Job> jobs = user.getCurrentJobs();

        if(jobs.size() == 0) {
            return ResponseEntity
                    .badRequest().body("");
        }

        List<JobResponse> response = new ArrayList<>();
        for(int i=0; i<jobs.size(); i++) {
            response.add(new JobResponse(jobs.get(i).getId(), jobs.get(i).getAddress(), jobs.get(i).getDescription(), user.getTotalJobs()-jobs.size() + i + 1));
        }

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
                        request.getDestinationLat(), null));
    }
}
