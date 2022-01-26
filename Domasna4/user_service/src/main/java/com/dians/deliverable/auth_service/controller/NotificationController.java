package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllNotifications() {
        notificationService.deleteAll();
    }
}
