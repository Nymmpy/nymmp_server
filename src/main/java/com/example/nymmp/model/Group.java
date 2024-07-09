package com.example.nymmp.model;

import javax.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "`groups`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long groupId;

    @Column(length = 100, nullable = false, unique = true)
    private String groupName;

    @Builder
    public Group(Long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
