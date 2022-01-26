package com.dians.deliverable.job_service.service;

import com.dians.deliverable.job_service.models.Job;
import com.dians.deliverable.job_service.models.JobStatus;
import com.dians.deliverable.job_service.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Job> getAllByStatus(JobStatus status) {
        return repository.findAllByStatus(status);
    }

    public Job getById(Long jobId) {
        return repository.getById(jobId);
    }

    public List<Job> getByDriverAndStatus(Long userId, JobStatus status) {
        return repository.findAllByAssignedToAndStatus(userId, status);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
