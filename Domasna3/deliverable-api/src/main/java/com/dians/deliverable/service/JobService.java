package com.dians.deliverable.service;

import com.dians.deliverable.models.AppUser;
import com.dians.deliverable.models.Job;
import com.dians.deliverable.models.JobStatus;
import com.dians.deliverable.repository.JobRepository;
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

    public List<Job> getByDriver(AppUser user) {
        return repository.findAllByAssignedTo(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
