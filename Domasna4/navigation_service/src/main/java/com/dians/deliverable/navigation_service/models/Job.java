package com.dians.deliverable.navigation_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    private Long id;
    private String address;
    private String description;
    private double lat;
    private double lon;
    private JobStatus status;
    private Long assignedTo;
    private Double distance;
}
