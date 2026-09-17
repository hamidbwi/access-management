package com.wwn.access_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Access {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;
}
