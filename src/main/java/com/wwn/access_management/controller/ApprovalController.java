package com.wwn.access_management.controller;

import com.wwn.access_management.dto.ApprovalActionRequest;
import com.wwn.access_management.dto.RequestResponse;
import com.wwn.access_management.service.ApprovalService;
import com.wwn.access_management.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<Page<RequestResponse>> getApprovals(
            Authentication authentication,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {

        Page<RequestResponse> response =
                approvalService.getApprovalRequests(
                        authentication.getName(),
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/action")
    public ResponseEntity<RequestResponse> processApproval(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalActionRequest request,
            Authentication authentication
    ) {

        RequestResponse response =
                approvalService.processApproval(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}
