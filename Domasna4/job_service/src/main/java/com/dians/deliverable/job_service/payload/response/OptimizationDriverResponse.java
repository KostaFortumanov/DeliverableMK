package com.dians.deliverable.job_service.payload.response;

import com.dians.deliverable.job_service.models.Job;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationDriverResponse {

    private String firstName;
    private String lastName;
    private List<Job> jobs = new ArrayList<>();
}
