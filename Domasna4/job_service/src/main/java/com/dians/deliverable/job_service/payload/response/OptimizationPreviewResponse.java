package com.dians.deliverable.job_service.payload.response;

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
public class OptimizationPreviewResponse {

    private String assignedJobs;
    private String driversUsed;
    private String fuelCost;
    private String time;
    private List<OptimizationDriverResponse> drivers = new ArrayList<>();
}
