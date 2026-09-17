package com.wwn.access_management.service;


import com.wwn.access_management.dto.ApprovalActionRequest;
import com.wwn.access_management.dto.RequestResponse;
import com.wwn.access_management.entity.AccessRequest;
import com.wwn.access_management.entity.User;
import com.wwn.access_management.enums.ApprovalAction;
import com.wwn.access_management.enums.RequestStatus;
import com.wwn.access_management.exception.ApprovalNotAllowedException;
import com.wwn.access_management.exception.ResourceNotFoundException;
import com.wwn.access_management.repository.AccessRequestRepository;
import com.wwn.access_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final AccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public RequestResponse processApproval(
            Long requestId,
            ApprovalActionRequest actionRequest,
            String username
    ) {

        User approver = findUser(username);

        AccessRequest request =
                accessRequestRepository
                        .findByIdWithUserAndAccess(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Access request not found: "
                                                + requestId
                                )
                        );

        validateApprovalPermission(
                approver,
                request
        );

        applyApprovalAction(
                approver,
                request,
                actionRequest
        );

        AccessRequest saved =
                accessRequestRepository.save(request);

        return toResponse(saved);
    }

    public Page<RequestResponse> getApprovalRequests(
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

        if (hasRole(user, "ADMIN")) {

            return accessRequestRepository
                    .findByStatus(
                            RequestStatus.PENDING_ADMIN,
                            pageable
                    )
                    .map(this::toResponse);
        }

        if (hasRole(user, "MANAGER")) {

            return accessRequestRepository
                    .findManagerApprovals(
                            RequestStatus.PENDING_MANAGER,
                            user.getId(),
                            pageable
                    )
                    .map(this::toResponse);
        }

        throw new AccessDeniedException(
                "User is not allowed to access approval inbox"
        );
    }

    private User findUser(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    private void validateApprovalPermission(
            User approver,
            AccessRequest request
    ) {

        RequestStatus status =
                request.getStatus();

        boolean isAdmin =
                hasRole(approver, "ADMIN");

        boolean isManager =
                hasRole(approver, "MANAGER");

        if (status == RequestStatus.APPROVED
                || status == RequestStatus.REJECTED) {

            throw new ApprovalNotAllowedException(
                    "Request has already been resolved"
            );
        }

        if (isManager
                && status == RequestStatus.PENDING_MANAGER) {

            if (!isManagerOfRequester(
                    approver,
                    request.getUser()
            )) {

                throw new ApprovalNotAllowedException(
                        "Manager cannot process request "
                                + "outside their team"
                );
            }

            return;
        }

        if (isAdmin
                && status == RequestStatus.PENDING_ADMIN) {

            return;
        }

        throw new ApprovalNotAllowedException(
                "Request is not at your approval stage"
        );
    }

    private void applyApprovalAction(
            User approver,
            AccessRequest request,
            ApprovalActionRequest actionRequest
    ) {

        if (actionRequest == null
                || actionRequest.getAction() == null) {

            throw new ApprovalNotAllowedException(
                    "Approval action is required"
            );
        }

        RequestStatus currentStatus =
                request.getStatus();

        ApprovalAction action =
                actionRequest.getAction();

        boolean isManager =
                hasRole(approver, "MANAGER");

        boolean isAdmin =
                hasRole(approver, "ADMIN");

        if (isManager
                && currentStatus
                == RequestStatus.PENDING_MANAGER) {

            if (action == ApprovalAction.APPROVE) {

                request.setStatus(
                        RequestStatus.PENDING_ADMIN
                );

            } else {

                request.setStatus(
                        RequestStatus.REJECTED
                );
            }

            request.setManagerNotes(
                    actionRequest.getNotes()
            );

            return;
        }

        if (isAdmin
                && currentStatus
                == RequestStatus.PENDING_ADMIN) {

            if (action == ApprovalAction.APPROVE) {

                request.setStatus(
                        RequestStatus.APPROVED
                );

            } else {

                request.setStatus(
                        RequestStatus.REJECTED
                );
            }

            request.setAdminNotes(
                    actionRequest.getNotes()
            );

            return;
        }

        throw new ApprovalNotAllowedException(
                "Invalid approval state transition"
        );
    }

    private boolean isManagerOfRequester(
            User manager,
            User requester
    ) {

        if (requester.getManager() == null) {
            return false;
        }

        return requester
                .getManager()
                .getId()
                .equals(manager.getId());
    }

    private boolean hasRole(
            User user,
            String roleName
    ) {

        return user.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getName()
                                .equals(roleName)
                );
    }

    private RequestResponse toResponse(
            AccessRequest request
    ) {

        return RequestResponse.builder()
                .id(request.getId())
                .accessId(
                        request.getAccess().getId()
                )
                .accessName(
                        request.getAccess().getName()
                )
                .reason(
                        request.getReason()
                )
                .status(
                        request.getStatus()
                )
                .createdAt(
                        request.getCreatedAt()
                )
                .updatedAt(
                        request.getUpdatedAt()
                )
                .build();
    }
}