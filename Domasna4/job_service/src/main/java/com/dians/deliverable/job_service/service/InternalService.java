package com.dians.deliverable.job_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class InternalService {

    @Value("${userServiceUrl}")
    private String userServiceUrl;

    @Value("${navigationServiceUrl}")
    private String navigationServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUserFullName(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(userServiceUrl + "/api/user/" + id + "/name", String.class);
    }

    public void setTotalJobs(Long userId, Integer jobs) {
        restTemplate.postForObject(userServiceUrl + "/api/user/setTotalJobs/" + userId, jobs, Void.class);
    }

    public String getVroomResponse(List<Long> driverIds) {
        return restTemplate.postForObject(navigationServiceUrl + "/api/optimization/vroomResponse", driverIds, String.class);
    }

}
