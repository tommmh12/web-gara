package com.webgara.module.notification.repository;

import com.webgara.module.notification.model.Notification;
import com.webgara.module.notification.model.NotificationEvent;
import com.webgara.module.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    Page<Notification> findByUserId(String userId, Pageable pageable);
    
    Page<Notification> findByUserIdAndIsRead(String userId, boolean isRead, Pageable pageable);
    
    long countByUserIdAndIsRead(String userId, boolean isRead);
    
    Page<Notification> findByUserIdAndEventAndType(String userId, NotificationEvent event, NotificationType type, Pageable pageable);
    
    List<Notification> findByReferenceTypeAndReferenceId(String referenceType, String referenceId);
    
    Page<Notification> findByGarageId(String garageId, Pageable pageable);
    
    @Query("{ 'userId': ?0, 'isRead': false }")
    List<Notification> findUnreadByUserId(String userId);
}
