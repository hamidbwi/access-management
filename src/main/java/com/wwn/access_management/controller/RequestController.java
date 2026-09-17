package com.wwn.access_management.controller;

import com.wwn.access_management.dto.ApprovalActionRequest;
import com.wwn.access_management.dto.CreateRequest;
import com.wwn.access_management.dto.RequestResponse;
import com.wwn.access_management.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody CreateRequest request,
            Authentication authentication
    ) {

        RequestResponse response =
                requestService.createRequest(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<RequestResponse>> getMyRequests(
            Authentication authentication,
            @PageableDefault(
                    page = 0,
                    size = 10
            ) Pageable pageable
    ) {

        Page<RequestResponse> response =
                requestService.getMyRequests(
                        authentication.getName(),
                        pageable
                );

        return ResponseEntity.ok(response);
    }
}
