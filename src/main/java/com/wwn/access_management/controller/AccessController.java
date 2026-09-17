package com.wwn.access_management.controller;

import com.wwn.access_management.dto.AccessResponse;
import com.wwn.access_management.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accesses")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @GetMapping
    public ResponseEntity<List<AccessResponse>> getAllAccesses() {

        return ResponseEntity.ok(
                accessService.getAllAccesses()
        );
    }
}