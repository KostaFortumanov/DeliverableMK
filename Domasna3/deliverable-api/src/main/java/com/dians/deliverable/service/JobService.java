package com.dians.deliverable.service;

import com.dians.deliverable.models.Job;
import com.dians.deliverable.models.JobStatus;
import com.dians.deliverable.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository repository;

    public JobService(JobRepository repository) {
        this.repository = repository;
    }

    public void save(Job job) {
        repository.save(job);
    }

    public boolean exists(String address, String description) {
        return repository.existsByAddressAndDescriptionAndStatus(address, description, JobStatus.NOT_ASSIGNED);
    }
}
