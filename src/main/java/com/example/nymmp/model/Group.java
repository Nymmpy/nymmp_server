package com.example.nymmp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    private String groupName;

}
