package com.webgara.module.repair.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposedItemDTO {
    @NotNull
    private String type;
    
    @NotBlank
    private String refId;
    
    @NotBlank
    private String name;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal unitPrice;
    
    @PositiveOrZero
    private BigDecimal discount;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal subtotal;
}
