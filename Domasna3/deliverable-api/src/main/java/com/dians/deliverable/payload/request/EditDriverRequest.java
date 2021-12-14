package com.dians.deliverable.payload.request;

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
