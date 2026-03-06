package com.webgara.module.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
@CompoundIndexes({
        @CompoundIndex(name = "garage_issuedAt_idx", def = "{'garageId': 1, 'issuedAt': -1}"),
})
public class Invoice {

    @Id
    private String id;

    @Indexed(unique = true)
    private String invoiceNumber;

    @Indexed
    private String appointmentId;

    @Indexed
    private String repairOrderId;

    @Indexed
    private String customerId;

    @Indexed
    private String garageId;

    private List<InvoiceItem> items;

    private BigDecimal subtotal;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal discountTotal;

    private BigDecimal totalAmount;

    private String currency;

    private String pdfUrl;

    @Indexed
    private InvoiceStatus status;

    private LocalDateTime dueDate;

    private String notes;

    private String issuedBy;

    private LocalDateTime issuedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceItem {
        private InvoiceItemType type;
        private String refId;
        private String name;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal subtotal;
    }
}
