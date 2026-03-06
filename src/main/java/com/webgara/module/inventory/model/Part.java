package com.webgara.module.inventory.model;

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
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parts")
@CompoundIndexes({
        @CompoundIndex(name = "garage_category_idx", def = "{'garageId': 1, 'category': 1}"),
        @CompoundIndex(name = "quantity_minQuantity_idx", def = "{'quantity': 1, 'minQuantity': 1}"),
})
public class Part {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    @TextIndexed
    private String name;

    private String description;

    @Indexed
    private PartCategory category;

    private String brand;

    private CompatibleVehicles compatibleVehicles;

    private Integer quantity;

    private Integer minQuantity;

    private String unit;

    private BigDecimal importPrice;

    private BigDecimal sellPrice;

    private Supplier supplier;

    private String location;

    @Indexed
    private String garageId;

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompatibleVehicles {
        private String brand;
        private List<String> models;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Supplier {
        private String name;
        private String phone;
        private String email;
    }
}
