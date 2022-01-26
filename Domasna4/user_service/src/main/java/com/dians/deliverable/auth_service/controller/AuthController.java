package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.AppUser;
import com.dians.deliverable.auth_service.models.UserRole;
import com.dians.deliverable.auth_service.payload.request.LoginRequest;
import com.dians.deliverable.auth_service.payload.request.NewAccountRequest;
import com.dians.deliverable.auth_service.payload.request.RegisterRequest;
import com.dians.deliverable.auth_service.payload.response.JwtResponse;
import com.dians.deliverable.auth_service.payload.response.MessageResponse;
import com.dians.deliverable.auth_service.security.NewAccountToken;
import com.dians.deliverable.auth_service.security.jwt.JwtUtils;
import com.dians.deliverable.auth_service.service.EmailService;
import com.dians.deliverable.auth_service.service.NewAccountTokenService;
import com.dians.deliverable.auth_service.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final NewAccountTokenService newAccountTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService, NewAccountTokenService newAccountTokenService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.newAccountTokenService = newAccountTokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername().toLowerCase().trim(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        AppUser user = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserRole().name()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

        System.out.println("register");
        if(userService.existsByEmail(registerRequest.getEmail().toLowerCase().trim())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use"));
        }

        if(!EmailValidator.getInstance().isValid(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid email"));
        }

        AppUser user = new AppUser();
        user.setEmail(registerRequest.getEmail().toLowerCase().trim());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        if(registerRequest.getUserRole().equalsIgnoreCase("driver")) {
            user.setUserRole(UserRole.DRIVER);
        } else if(registerRequest.getUserRole().equalsIgnoreCase("manager")) {
            user.setUserRole(UserRole.MANAGER);
        }
        user.setEnabled(false);

        String token = UUID.randomUUID().toString();
        NewAccountToken newAccountToken = new NewAccountToken(token, user);

        userService.save(user);
        newAccountTokenService.save(newAccountToken);

        emailService.sendNewAccountEmail(user.getEmail(), token);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/newAccount")
    public ResponseEntity<?> newAccount(@RequestBody NewAccountRequest newAccountRequest) {

        if(!newAccountRequest.getPassword().equals(newAccountRequest.getRepeatPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: passwords dont match"));
        }

        NewAccountToken token = newAccountTokenService.getToken(newAccountRequest.getToken());
        if(token == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Token doesn't exist or has expired"));
        }

        AppUser user = token.getUser();
        user.setPassword(passwordEncoder.encode(newAccountRequest.getPassword()));
        user.setEnabled(true);
        userService.save(user);

        newAccountTokenService.delete(token);

        return ResponseEntity.ok(new MessageResponse("Account created successfully"));
    }
}
