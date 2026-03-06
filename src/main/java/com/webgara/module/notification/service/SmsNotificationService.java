package com.webgara.module.notification.service;

import com.webgara.module.notification.dto.NotificationRequest;

public interface SmsNotificationService {
    
    void sendSms(NotificationRequest request);
    
    void sendSmsAsync(NotificationRequest request);
}
