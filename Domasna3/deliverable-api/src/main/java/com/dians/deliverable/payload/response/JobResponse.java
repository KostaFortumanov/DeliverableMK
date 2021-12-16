package com.dians.deliverable.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String address;
    private String description;
    private String driver;

    public JobResponse(Long id, String address, String description) {
        this.id = id;
        this.address = address;
        this.description = description;
        driver = "";
    }
}
