package com.wwn.access_management.repository;

import com.wwn.access_management.entity.AccessRequest;
import com.wwn.access_management.enums.RequestStatus;
import com.wwn.access_management.repository.projection.DashboardMetricsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccessRequestRepository  extends JpaRepository<AccessRequest, Long> {

    Page<AccessRequest> findByUserId(
            Long userId,
            Pageable pageable
    );

    Page<AccessRequest> findByStatus(
            RequestStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT r
        FROM AccessRequest r
        JOIN r.user u
        WHERE r.status = :status
          AND u.manager.id = :managerId
          ORDER BY r.createdAt ASC
    """)
    Page<AccessRequest> findManagerApprovals(
            @Param("status") RequestStatus status,
            @Param("managerId") Long managerId,
            Pageable pageable
    );

    @Query("""
    SELECT r
    FROM AccessRequest r
    JOIN FETCH r.user u
    JOIN FETCH r.access a
    WHERE r.id = :id
""")
    Optional<AccessRequest> findByIdWithUserAndAccess(
            @Param("id") Long id
    );

    @Query(
            value = """
        SELECT
            COUNT(*) AS total_requests,

            COUNT(*) FILTER (
                WHERE status IN (
                    'PENDING_MANAGER',
                    'PENDING_ADMIN'
                )
            ) AS in_progress,

            COUNT(*) FILTER (
                WHERE status = 'PENDING_MANAGER'
            ) AS waiting_manager,

            COUNT(*) FILTER (
                WHERE status = 'PENDING_ADMIN'
            ) AS waiting_admin,

            COUNT(*) FILTER (
                WHERE status = 'APPROVED'
            ) AS approved,

            COUNT(*) FILTER (
                WHERE status = 'REJECTED'
            ) AS rejected

        FROM access_requests
        """,
            nativeQuery = true
    )
    DashboardMetricsProjection getDashboardMetrics();
}
