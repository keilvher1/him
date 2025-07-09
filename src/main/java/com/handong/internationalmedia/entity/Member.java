package com.handong.internationalmedia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 50)
    private String studentId;

    @Column(length = 50)
    private String department;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(length = 500)
    private String bio;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        joinDate = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    public enum MemberRole {
        PRESIDENT,
        VICE_PRESIDENT,
        SECRETARY,
        TREASURER,
        MEMBER
    }
}