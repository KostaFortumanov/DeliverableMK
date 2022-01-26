package com.dians.deliverable.job_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class InternalService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUserFullName(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:9091/api/user/" + id + "/name", String.class);
    }

    public String getVroomResponse(List<Long> driverIds) {
        return restTemplate.postForObject("http://localhost:9093/api/optimization/vroomResponse", driverIds, String.class);
    }

    public void setTotalJobs(Long userId, Integer jobs) {
        restTemplate.postForObject("http://localhost:9091/api/user/setTotalJobs/" + userId, jobs, Void.class);
    }
}
