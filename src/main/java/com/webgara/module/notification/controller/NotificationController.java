package com.webgara.module.notification.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.dto.NotificationResponse;
import com.webgara.module.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(@PathVariable String id) {
        NotificationResponse response = notificationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable String id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<NotificationResponse> response = notificationService.getByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<NotificationResponse> response = notificationService.getUnreadByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(@PathVariable String userId) {
        long count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @GetMapping("/garage/{garageId}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getGarageNotifications(
            @PathVariable String garageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<NotificationResponse> response = notificationService.getByGarageId(garageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
