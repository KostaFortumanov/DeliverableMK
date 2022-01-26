package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getUserFullName(@PathVariable Long id) {
        AppUser user = userService.getById(id);
        return ResponseEntity
                .ok(user.getFirstName() + " " + user.getLastName());
    }
}
