package com.webgara.module.notification.service;

import com.webgara.module.notification.dto.NotificationRequest;

public interface EmailNotificationService {
    
    void sendEmail(NotificationRequest request);
    
    void sendEmailAsync(NotificationRequest request);
}
