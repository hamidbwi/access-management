package com.wwn.access_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {

    @JsonProperty("total_requests")
    private long totalRequests;

    @JsonProperty("in_progress")
    private long inProgress;

    @JsonProperty("waiting_manager")
    private long waitingManager;

    @JsonProperty("waiting_admin")
    private long waitingAdmin;

    @JsonProperty("approved")
    private long approved;

    @JsonProperty("rejected")
    private long rejected;
}
