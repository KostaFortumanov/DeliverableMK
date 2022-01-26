package com.dians.deliverable.navigation_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCurrentPathRequest {

    private double lon;
    private double lat;
    private double destinationLon;
    private double destinationLat;
}
