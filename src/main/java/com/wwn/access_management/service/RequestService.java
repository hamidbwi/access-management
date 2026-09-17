package com.wwn.access_management.service;

import com.wwn.access_management.dto.CreateRequest;
import com.wwn.access_management.dto.RequestResponse;
import com.wwn.access_management.entity.Access;
import com.wwn.access_management.entity.AccessRequest;
import com.wwn.access_management.entity.User;
import com.wwn.access_management.enums.RequestStatus;
import com.wwn.access_management.exception.ResourceNotFoundException;
import com.wwn.access_management.repository.AccessRequestRepository;
import com.wwn.access_management.repository.AccessRepository;
import com.wwn.access_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final AccessRequestRepository accessRequestRepository;

    private final AccessRepository accessRepository;

    private final UserRepository userRepository;

    @Transactional
    public RequestResponse createRequest(
            CreateRequest request,
            String username
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );

        Access access = accessRepository
                .findById(request.getAccessId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Access not found: "
                                        + request.getAccessId()
                        )
                );

        AccessRequest accessRequest =
                AccessRequest.builder()
                        .user(user)
                        .access(access)
                        .reason(request.getReason())
                        .status(RequestStatus.PENDING_MANAGER)
                        .build();

        AccessRequest saved =
                accessRequestRepository.save(accessRequest);

        return toResponse(saved);
    }

    private RequestResponse toResponse(
            AccessRequest request
    ) {

        return RequestResponse.builder()
                .id(request.getId())
                .accessId(request.getAccess().getId())
                .accessName(request.getAccess().getName())
                .reason(request.getReason())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    public Page<RequestResponse> getMyRequests(
            String username,
            Pageable pageable
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );

        return accessRequestRepository
                .findByUserId(user.getId(), pageable)
                .map(this::toResponse);
    }

}
