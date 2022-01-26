package com.dians.deliverable.auth_service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalDashboardResponse extends DayResponse {

    String name;

    public TotalDashboardResponse() {
    }

    public TotalDashboardResponse(String name) {
        this.name = name;
    }

    public TotalDashboardResponse(double fuel, double distance, double numJobs, String name) {
        super(fuel, distance, numJobs);
        this.name = name;
    }
}
