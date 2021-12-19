package com.dians.deliverable.controller;

import com.dians.deliverable.models.Notification;
import com.dians.deliverable.payload.request.ManagerMapRequest;
import com.dians.deliverable.payload.response.MessageResponse;
import com.dians.deliverable.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public MessageController(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @MessageMapping("/manager")
    public void processMessage() {
        messagingTemplate.convertAndSendToUser("12","/queue/messages", new MessageResponse("zdravo"));
    }

    @MessageMapping("/manager/finishJob")
    public void finishedJob(@RequestBody String message) {
        notificationService.save(new Notification(message));
        messagingTemplate.convertAndSendToUser("12","/queue/messages", new MessageResponse(message));
    }

    @MessageMapping("/manager/map")
    public void userPath(@Payload ManagerMapRequest request) {
        System.out.println(request.getName());
        messagingTemplate.convertAndSendToUser("13","/queue/messages", request);
    }
}
