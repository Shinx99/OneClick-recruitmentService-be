package com.onceClick.recruitmentService.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response format")
public class ErrorResponse {

    @Schema(description = "Error timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Field validation errors (optional)")
    private Map<String, String> validationErrors;
}
