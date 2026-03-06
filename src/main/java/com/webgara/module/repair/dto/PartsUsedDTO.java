package com.webgara.module.repair.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartsUsedDTO {
    @NotBlank
    private String partId;
    
    @NotBlank
    private String partName;
    
    @NotNull
    @Positive
    private Integer quantity;
}
