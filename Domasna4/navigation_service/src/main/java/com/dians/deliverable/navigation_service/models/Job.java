package com.dians.deliverable.navigation_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public Job() {
    }

    public Job(String address, String description, double lat, double lon, JobStatus status) {
        this.address = address;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
    }
}
