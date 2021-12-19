package com.dians.deliverable.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TotalDashboardResponse {

    String name;
    double fuel;
    double distance;
    double numJobs;

    public TotalDashboardResponse() {
    }

    public TotalDashboardResponse(String name) {
        this.name = name;
    }
}
