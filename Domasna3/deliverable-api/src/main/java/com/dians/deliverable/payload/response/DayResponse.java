package com.dians.deliverable.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DayResponse {

    double fuel;
    double distance;
    double numJobs;

    public DayResponse() {
    }
}