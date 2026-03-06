package com.webgara.module.invoice.dto;

import com.webgara.module.invoice.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequest {
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private Map<String, Object> gatewayResponse;
}
