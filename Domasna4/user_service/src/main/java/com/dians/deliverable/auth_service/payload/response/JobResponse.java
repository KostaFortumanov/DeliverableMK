package com.dians.deliverable.auth_service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String address;
    private String description;
    private String driver;
    private int order;
}
