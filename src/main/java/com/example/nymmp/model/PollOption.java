package com.example.nymmp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option1_id", nullable = false)
    private User option1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option2_id", nullable = false)
    private User option2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option3_id", nullable = false)
    private User option3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option4_id", nullable = false)
    private User option4;
}
