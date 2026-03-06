package com.webgara.module.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {

    @NotBlank(message = "Reply content is required")
    private String content;

    @NotBlank(message = "User ID is required")
    private String repliedBy;
}
