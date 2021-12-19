package com.dians.deliverable.controller;

import com.dians.deliverable.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "${frontUrl}", maxAge = 3600)
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
