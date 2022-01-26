package com.dians.deliverable.auth_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String userRole;
}
