package com.webgara.module.invoice.dto;

import com.webgara.module.invoice.model.PaymentMethod;
import com.webgara.module.invoice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String invoiceId;
    private String customerId;
    private String garageId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private Map<String, Object> gatewayResponse;
    private BankInfoDTO bankInfo;
    private String refundReason;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private String paymentUrl;
}
