package com.dians.deliverable.navigation_service.service;

import com.dians.deliverable.navigation_service.exceptions.NoJobsException;
import com.dians.deliverable.navigation_service.models.AppUser;
import com.dians.deliverable.navigation_service.models.Job;
import com.dians.deliverable.navigation_service.models.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InternalService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Job> getUnassignedJobs() {
        Job[] response = restTemplate.getForObject("http://localhost:9092/api/jobs/unassigned", Job[].class);
        if (response != null) {
            return Arrays.asList(response);
        }

        return new ArrayList<>();
    }

    public AppUser getDriver(Long driverId) {
        return restTemplate.getForObject("http://localhost:9091/api/user/" + driverId, AppUser.class);
    }

    public void setJobDistance(Long jobId, Double distance) {
        restTemplate.postForObject("http://localhost:9092/api/jobs/setJobDistance/" + jobId, distance, Void.class);
    }

    public List<Job> getCurrentDriverJobs(Long driverId) {
        Job[] response = restTemplate.getForObject("http://localhost:9092/api/jobs/driver/" + driverId, Job[].class);
        if (response != null) {
            return Arrays.asList(response);
        }

        return new ArrayList<>();
    }

    public Job getJob(Long jobId) {
        return restTemplate.getForObject("http://localhost:9092/api/jobs/" + jobId, Job.class);
    }

    public void finishJob(Long jobId) {
        restTemplate.getForObject("http://localhost:9092/api/jobs/finishJob/" + jobId, Void.class);
    }

    public void saveStatistics(Statistics statistics) {
        restTemplate.postForObject("http://localhost:9091/api/user/saveStatistics", statistics, Void.class);
    }
}
