package com.dians.deliverable.navigation_service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPathResponse {

    private double distance;
    private double time;
    private List<List<Double>> path;

    public GetPathResponse() {
    }
}
