package com.dians.deliverable.auth_service;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.UserRole;
import com.dians.deliverable.auth_service.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class AuthServiceApplication {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceApplication(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
    
    @PostConstruct
    public void addUsers() {

        if(!userService.existsByEmail("manager@mail.com")) {
            userService.save(
                    new AppUser(
                            null,
                            "Manager",
                            "1",
                            "manager@mail.com",
                            "123456789",
                            passwordEncoder.encode("manager"),
                            UserRole.MANAGER,
                            true,
                            0));
        }

        if(!userService.existsByEmail("driver@mail.com")) {
            userService.save(
                    new AppUser(
                            null,
                            "driver",
                            "1",
                            "driver@mail.com",
                            "987654321",
                            passwordEncoder.encode("driver"),
                            UserRole.DRIVER,
                            true,
                            0));
        }
    }

}
