package com.example.nymmp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pollId;

    private String question;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
