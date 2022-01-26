package com.dians.deliverable.navigation_service.controller;

import com.dians.deliverable.navigation_service.models.AppUser;
import com.dians.deliverable.navigation_service.models.Config;
import com.dians.deliverable.navigation_service.models.Job;
import com.dians.deliverable.navigation_service.models.Statistics;
import com.dians.deliverable.navigation_service.payload.request.UpdateCurrentPathRequest;
import com.dians.deliverable.navigation_service.payload.response.JobResponse;
import com.dians.deliverable.navigation_service.service.ConfigService;
import com.dians.deliverable.navigation_service.service.InternalService;
import com.dians.deliverable.navigation_service.service.RouteFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {

    private final RouteFinderService routeFinderService;
    private final ConfigService configService;
    private final InternalService internalService;

    public MapController(RouteFinderService routeFinderService, ConfigService configService, InternalService internalService) {
        this.routeFinderService = routeFinderService;
        this.configService = configService;
        this.internalService = internalService;
    }

    @GetMapping("/allPaths")
    public ResponseEntity<?> getAllPaths(@RequestHeader String userId) {

        Long id = Long.parseLong(userId);
        List<Job> jobs = internalService.getCurrentDriverJobs(id);

        if(jobs.size() == 0) {
            return ResponseEntity
                    .badRequest().body("");
        }

        Config config = configService.getConfig();
        double startLon = config.getStartLon();
        double startLat = config.getStartLat();

        return ResponseEntity
                .ok(routeFinderService.getPaths(startLon, startLat, jobs));

    }

    @GetMapping("/finishJob/{jobId}")
    public ResponseEntity<?> finishJob(@RequestHeader String userId, @PathVariable Long jobId) {
        Job job = internalService.getJob(jobId);
        internalService.finishJob(jobId);

        double distance = job.getDistance();
        double fuel = distance/100000 * 8;

        Long id = Long.parseLong(userId);
        internalService.saveStatistics(new Statistics(fuel, distance, LocalDate.now(), id));
        return ResponseEntity.ok("");
    }

    @GetMapping("/currentJobs")
    public ResponseEntity<?> getCurrentJobs(@RequestHeader String userId) {
        Long id = Long.parseLong(userId);
        AppUser user = internalService.getDriver(id);
        List<Job> jobs = internalService.getCurrentDriverJobs(id);

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

        return ResponseEntity
                .ok(routeFinderService.getPath(
                        request.getLon(),
                        request.getLat(),
                        request.getDestinationLon(),
                        request.getDestinationLat(), null));
    }
}
