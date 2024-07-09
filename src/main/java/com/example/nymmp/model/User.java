package com.example.nymmp.model;

import javax.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long userId;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 45, nullable = false)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(columnDefinition = "BIGINT")
    private Long kakaoId;

    @Builder
    public User(Long userId, String email, String password, String username, Group group, Long kakaoId) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.group = group;
        this.kakaoId = kakaoId;
    }

    public void assignGroup(Group group) {
        this.group = group;
    }

}
