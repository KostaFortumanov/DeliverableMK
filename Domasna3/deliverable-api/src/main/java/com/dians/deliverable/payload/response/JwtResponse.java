package com.dians.deliverable.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
