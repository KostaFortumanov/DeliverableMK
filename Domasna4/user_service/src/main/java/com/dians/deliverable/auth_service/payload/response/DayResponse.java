package com.dians.deliverable.auth_service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayResponse {

    double fuel;
    double distance;
    double numJobs;
}
