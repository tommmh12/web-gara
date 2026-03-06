package com.webgara.module.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    private String appointmentId;
    private String repairOrderId;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Garage ID is required")
    private String garageId;
    
    @NotEmpty(message = "Invoice items cannot be empty")
    private List<InvoiceItemDTO> items;
    
    @NotNull(message = "Tax rate is required")
    @PositiveOrZero(message = "Tax rate must be positive or zero")
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("10");
    
    private LocalDateTime dueDate;
    private String notes;
    
    @NotBlank(message = "Issued by user ID is required")
    private String issuedBy;
}
