package com.dians.deliverable.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String userRole;

    public RegisterRequest() {
    }
}
