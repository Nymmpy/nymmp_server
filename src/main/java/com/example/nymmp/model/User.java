package com.example.nymmp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String username;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
