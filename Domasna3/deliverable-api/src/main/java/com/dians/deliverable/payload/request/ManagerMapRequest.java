package com.dians.deliverable.payload.request;

import com.dians.deliverable.payload.response.JobResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ManagerMapRequest {

    private Long id;
    private String name;
    private double currentLon;
    private double currentLat;
    private double destinationLon;
    private double destinationLat;
    private JobResponse job;

    public ManagerMapRequest() {
    }
}
