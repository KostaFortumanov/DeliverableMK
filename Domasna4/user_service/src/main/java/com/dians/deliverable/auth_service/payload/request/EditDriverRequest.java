package com.dians.deliverable.auth_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class EditDriverRequest {

    private Long id;
    private String newEmail;
    private String newPhonenumber;

    public EditDriverRequest() {
    }
}
