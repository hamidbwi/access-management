package com.wwn.access_management.dto;

import com.wwn.access_management.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {

    private Long id;

    private Long accessId;

    private String accessName;

    private String reason;

    private RequestStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}