package com.dians.deliverable.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SelectDriverResponse {

    private Long id;
    private String fullName;
}
