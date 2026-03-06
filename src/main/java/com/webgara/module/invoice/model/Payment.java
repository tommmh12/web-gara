package com.webgara.module.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
@CompoundIndexes({
        @CompoundIndex(name = "garage_paidAt_idx", def = "{'garageId': 1, 'paidAt': -1}"),
})
public class Payment {

    @Id
    private String id;

    @Indexed
    private String invoiceId;

    @Indexed
    private String customerId;

    @Indexed
    private String garageId;

    private BigDecimal amount;

    private String currency;

    @Indexed
    private PaymentMethod method;

    @Indexed
    private PaymentStatus status;

    @Indexed
    private String transactionId;

    private Map<String, Object> gatewayResponse;

    private BankInfo bankInfo;

    private String refundReason;

    private LocalDateTime paidAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankInfo {
        private String bankName;
        private String accountNumber;
        private String transferNote;
    }
}
