package com.wwn.access_management.service;

import com.wwn.access_management.dto.DashboardMetricsResponse;
import com.wwn.access_management.dto.DashboardMetricsResponse;
import com.wwn.access_management.repository.AccessRequestRepository;
import com.wwn.access_management.repository.projection.DashboardMetricsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccessRequestRepository accessRequestRepository;

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getMetrics() {

        DashboardMetricsProjection metrics =
                accessRequestRepository
                        .getDashboardMetrics();

        return DashboardMetricsResponse.builder()
                .totalRequests(
                        metrics.getTotalRequests()
                )
                .inProgress(
                        metrics.getInProgress()
                )
                .waitingManager(
                        metrics.getWaitingManager()
                )
                .waitingAdmin(
                        metrics.getWaitingAdmin()
                )
                .approved(
                        metrics.getApproved()
                )
                .rejected(
                        metrics.getRejected()
                )
                .build();
    }
}