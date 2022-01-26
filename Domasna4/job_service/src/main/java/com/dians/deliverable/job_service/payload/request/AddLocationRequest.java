package com.dians.deliverable.job_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddLocationRequest {
    private String city;
    private String street;
    private String number;
    private double lat;
    private double lon;

    public AddLocationRequest() {
    }
}