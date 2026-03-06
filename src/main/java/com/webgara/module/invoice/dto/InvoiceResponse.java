package com.webgara.module.invoice.dto;

import com.webgara.module.invoice.model.InvoiceStatus;
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
public class InvoiceResponse {
    private String id;
    private String invoiceNumber;
    private String appointmentId;
    private String repairOrderId;
    private String customerId;
    private String garageId;
    private List<InvoiceItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountTotal;
    private BigDecimal totalAmount;
    private String currency;
    private String pdfUrl;
    private InvoiceStatus status;
    private LocalDateTime dueDate;
    private String notes;
    private String issuedBy;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
