package com.wwn.access_management.repository;
import com.wwn.access_management.entity.ApprovalAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalActionRepository
        extends JpaRepository<ApprovalAction, Long> {
}
