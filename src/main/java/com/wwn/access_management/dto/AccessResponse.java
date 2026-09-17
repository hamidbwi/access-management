package com.wwn.access_management.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessResponse {

    private Long id;

    private String name;

    private String description;
}