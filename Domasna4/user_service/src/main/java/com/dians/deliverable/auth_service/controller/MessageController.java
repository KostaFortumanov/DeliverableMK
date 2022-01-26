package com.dians.deliverable.auth_service.controller;

import com.dians.deliverable.auth_service.models.Notification;
import com.dians.deliverable.auth_service.payload.request.ManagerMapRequest;
import com.dians.deliverable.auth_service.payload.response.MessageResponse;
import com.dians.deliverable.auth_service.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public MessageController(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @MessageMapping("/manager/finishJob")
    public void finishedJob(@RequestBody String message) {
        notificationService.save(new Notification(message));
        messagingTemplate.convertAndSendToUser("managerJob","/queue/messages", new MessageResponse(message));
    }

    @MessageMapping("/manager/map")
    public void userPath(@Payload ManagerMapRequest request) {
        messagingTemplate.convertAndSendToUser("managerMap","/queue/messages", request);
    }
}
