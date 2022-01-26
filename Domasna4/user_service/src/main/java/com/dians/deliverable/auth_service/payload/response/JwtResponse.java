package com.dians.deliverable.auth_service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
