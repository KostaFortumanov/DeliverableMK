package com.dians.deliverable.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCurrentPathRequest {

    private double lon;
    private double lat;
    private double destinationLon;
    private double destinationLat;

    public UpdateCurrentPathRequest() {
    }
}
