package com.webgara.module.notification.mapper;

import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.dto.NotificationResponse;
import com.webgara.module.notification.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "sentSuccessfully", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Notification toEntity(NotificationRequest request);
    
    NotificationResponse toResponse(Notification notification);
    
    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
