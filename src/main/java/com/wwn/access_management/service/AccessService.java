package com.wwn.access_management.service;

import com.wwn.access_management.dto.AccessResponse;
import com.wwn.access_management.entity.Access;
import com.wwn.access_management.repository.AccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final AccessRepository accessRepository;

    public List<AccessResponse> getAllAccesses() {

        return accessRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AccessResponse toResponse(Access access) {

        return new AccessResponse(
                access.getId(),
                access.getName(),
                access.getDescription()
        );
    }
}
