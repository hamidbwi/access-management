package com.wwn.access_management.repository.projection;

public interface DashboardMetricsProjection {
    long getTotalRequests();

    long getInProgress();

    long getWaitingManager();

    long getWaitingAdmin();

    long getApproved();

    long getRejected();
}
