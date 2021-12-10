package com.dians.deliverable.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${frontUrl}")
    private String frontUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNewAccountEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Account");

        String text = "Finish creating your account on the following link\n" +
                frontUrl + "/newAccount?token=" + token;
        message.setText(text);
        mailSender.send(message);
    }
}
