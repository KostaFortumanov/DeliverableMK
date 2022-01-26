package com.dians.deliverable.auth_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewAccountRequest {

    private String token;
    private String password;
    private String repeatPassword;
}
