package com.wwn.access_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {

    @NotNull(message = "access_id is required")
    @JsonProperty("access_id")
    private Long accessId;

    @NotBlank(message = "reason is required")
    private String reason;
}
