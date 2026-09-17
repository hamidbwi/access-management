package com.wwn.access_management.dto;

import com.wwn.access_management.enums.ApprovalAction;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionRequest {

    @NotNull(message = "action is required")
    private ApprovalAction action;

    private String notes;


}
