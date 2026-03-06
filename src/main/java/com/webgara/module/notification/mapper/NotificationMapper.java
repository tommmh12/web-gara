package com.webgara.module.notification.mapper;

import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.dto.NotificationResponse;
import com.webgara.module.notification.model.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    Notification toEntity(NotificationRequest request);
    
    NotificationResponse toResponse(Notification notification);
    
    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
