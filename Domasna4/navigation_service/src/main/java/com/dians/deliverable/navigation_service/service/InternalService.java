package com.dians.deliverable.navigation_service.service;

import com.dians.deliverable.navigation_service.models.AppUser;
import com.dians.deliverable.navigation_service.models.Job;
import com.dians.deliverable.navigation_service.models.Statistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InternalService {

    @Value("${userServiceUrl}")
    private String userServiceUrl;

    @Value("${jobServiceUrl}")
    private String jobServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AppUser getDriver(Long driverId) {
        return restTemplate.getForObject(userServiceUrl + "/api/user/" + driverId, AppUser.class);
    }

    public void saveStatistics(Statistics statistics) {
        restTemplate.postForObject(userServiceUrl + "/api/user/saveStatistics", statistics, Void.class);
    }

    public List<Job> getUnassignedJobs() {
        Job[] response = restTemplate.getForObject(jobServiceUrl + "/api/jobs/unassigned", Job[].class);
        if (response != null) {
            return Arrays.asList(response);
        }

        return new ArrayList<>();
    }

    public void setJobDistance(Long jobId, Double distance) {
        restTemplate.postForObject(jobServiceUrl + "/api/jobs/setJobDistance/" + jobId, distance, Void.class);
    }

    public List<Job> getCurrentDriverJobs(Long driverId) {
        Job[] response = restTemplate.getForObject(jobServiceUrl + "/api/jobs/driver/" + driverId, Job[].class);
        if (response != null) {
            return Arrays.asList(response);
        }

        return new ArrayList<>();
    }

    public Job getJob(Long jobId) {
        return restTemplate.getForObject(jobServiceUrl + "/api/jobs/" + jobId, Job.class);
    }

    public void finishJob(Long jobId) {
        restTemplate.getForObject(jobServiceUrl + "/api/jobs/finishJob/" + jobId, Void.class);
    }
}
