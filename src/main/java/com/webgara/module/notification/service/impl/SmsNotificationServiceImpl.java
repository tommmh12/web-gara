package com.webgara.module.notification.service.impl;

import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.service.SmsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationServiceImpl implements SmsNotificationService {
    
    @Override
    public void sendSms(NotificationRequest request) {
        log.info("Sending SMS notification to: {} - Message: {}", request.getRecipient(), request.getMessage());
        // Mock implementation - in production, use Twilio or similar SMS provider
        // Example:
        // Message message = messageCreator.create(new PhoneNumber("+1" + request.getRecipient()),
        //     new PhoneNumber(TWILIO_PHONE_NUMBER),
        //     request.getMessage())
        // .create();
        log.debug("SMS sent successfully to: {}", request.getRecipient());
    }
    
    @Override
    @Async
    public void sendSmsAsync(NotificationRequest request) {
        sendSms(request);
    }
}
