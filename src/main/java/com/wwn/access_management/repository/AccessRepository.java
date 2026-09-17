package com.wwn.access_management.repository;
import com.wwn.access_management.entity.Access;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRepository
        extends JpaRepository<Access, Long> {
}
