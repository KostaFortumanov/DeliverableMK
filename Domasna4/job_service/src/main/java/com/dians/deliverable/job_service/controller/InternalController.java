package com.dians.deliverable.job_service.controller;

import com.dians.deliverable.job_service.models.Job;
import com.dians.deliverable.job_service.models.JobStatus;
import com.dians.deliverable.job_service.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class InternalController {

    private final JobService jobService;

    public InternalController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/removeJobAssignment/{userId}")
    public void removeJobAssignment(@PathVariable Long userId) {
        List<Job> jobs = jobService.getByDriver(userId);
        jobs.forEach(job -> job.setAssignedTo(null));
        jobService.saveAll(jobs);
    }

    @PostMapping("/setJobDistance/{jobId}")
    public void setJobDistance(@PathVariable Long jobId, @RequestBody Double distance) {
        Job job = jobService.getById(jobId);
        job.setDistance(distance);
        jobService.save(job);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<?> getUnassigned() {
        return ResponseEntity
                .ok(jobService.getAllByStatus(JobStatus.NOT_ASSIGNED));
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<?> getDriverJobs(@PathVariable Long id) {
        return ResponseEntity
                .ok(jobService.getByDriverAndStatus(id, JobStatus.ASSIGNED));
    }

    @GetMapping("/finishJob/{id}")
    public void finishJob(@PathVariable Long id) {
        Job job = jobService.getById(id);
        job.setStatus(JobStatus.COMPLETED);
        jobService.save(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        return ResponseEntity
                .ok(jobService.getById(id));
    }
}
