package com.webgara.module.service.dto;

import com.webgara.module.service.model.ServiceItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItemResponse {
    private String id;
    private String garageId;
    private String name;
    private String description;
    private String category;
    private Integer standardTime;
    private List<ServiceItem.Pricing> pricing;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
