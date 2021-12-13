package com.dians.deliverable.repository;

import com.dians.deliverable.models.Job;
import com.dians.deliverable.models.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByAddressAndDescriptionAndStatus(String address, String description, JobStatus jobStatus);
    List<Job> findAllByStatus(JobStatus status);
}
