package com.dians.deliverable.service;

import com.dians.deliverable.models.Notification;
import com.dians.deliverable.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public void deleteAll() {
        notificationRepository.deleteAll();
    }
}
