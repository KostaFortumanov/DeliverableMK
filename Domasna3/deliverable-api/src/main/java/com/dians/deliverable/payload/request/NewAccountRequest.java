package com.dians.deliverable.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewAccountRequest {

    private String token;
    private String password;
    private String repeatPassword;

    public NewAccountRequest() {
    }
}
