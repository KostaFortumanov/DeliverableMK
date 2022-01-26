package com.dians.deliverable.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InternalService {

    @Value("${jobServiceUrl}")
    private String jobServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void removeJobAssignment(Long userId) {
        restTemplate.getForEntity(jobServiceUrl + "/api/jobs/removeJobAssignment/" + userId, Void.class);
    }
}
