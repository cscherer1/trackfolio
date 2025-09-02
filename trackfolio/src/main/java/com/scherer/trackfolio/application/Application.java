package com.scherer.trackfolio.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scherer.trackfolio.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore // prevent recursion / serialization errors
    private User owner;

    @Column(nullable = false, length = 160)
    private String company;

    @Column(name = "role_title", nullable = false, length = 160)
    private String roleTitle;

    // store as strings (valid values enforced at DB via CHECK; we can refactor to enums later)
    @Column(nullable = false, length = 40)
    private String source;   // LINKEDIN | COMPANY | INDEED | REFERRAL | OTHER

    @Column(nullable = false, length = 32)
    private String status;   // APPLIED | INTERVIEW | OFFER | REJECTED | WITHDRAWN

    @Column(name = "applied_at", nullable = false)
    private LocalDate appliedAt;

    @Column(length = 160)
    private String location;

    @Column(name = "salary_text", length = 120)
    private String salaryText;

    @Column(name = "job_link", columnDefinition = "TEXT")
    private String jobLink;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
