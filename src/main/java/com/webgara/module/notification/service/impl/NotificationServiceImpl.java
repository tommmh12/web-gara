package com.webgara.module.notification.service.impl;

import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.dto.NotificationResponse;
import com.webgara.module.notification.mapper.NotificationMapper;
import com.webgara.module.notification.model.Notification;
import com.webgara.module.notification.repository.NotificationRepository;
import com.webgara.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    
    @Override
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .garageId(request.getGarageId())
                .type(request.getType())
                .event(request.getEvent())
                .title(request.getTitle())
                .message(request.getMessage())
                .recipient(request.getRecipient())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .isRead(false)
                .sentSuccessfully(true)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification with ID: {}", savedNotification.getId());
        return notificationMapper.toResponse(savedNotification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        return notificationMapper.toResponse(notification);
    }
    
    @Override
    public NotificationResponse markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Marked notification with ID: {} as read", id);
        return notificationMapper.toResponse(updatedNotification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByUserId(String userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadByUserId(String userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false, pageable);
        return notifications.map(notificationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUserId(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByGarageId(String garageId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByGarageId(garageId, pageable);
        return notifications.map(notificationMapper::toResponse);
    }
    
    @Override
    public void delete(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        
        notificationRepository.delete(notification);
        log.info("Deleted notification with ID: {}", id);
    }
}
