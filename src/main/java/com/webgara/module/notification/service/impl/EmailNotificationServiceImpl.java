package com.webgara.module.notification.service.impl;

import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {
    
    @Override
    public void sendEmail(NotificationRequest request) {
        log.info("Sending email notification to: {} - Title: {}", request.getRecipient(), request.getTitle());
        // Mock implementation - in production, use JavaMailSender or third-party email service
        // Example:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(request.getRecipient());
        // message.setSubject(request.getTitle());
        // message.setText(request.getMessage());
        // mailSender.send(message);
        log.debug("Email sent successfully to: {}", request.getRecipient());
    }
    
    @Override
    @Async
    public void sendEmailAsync(NotificationRequest request) {
        sendEmail(request);
    }
}
