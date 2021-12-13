package com.dians.deliverable.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddJobRequest {

    private String city;
    private String street;
    private String number;
    private String description;

    public AddJobRequest() {
    }
}
