package com.scherer.trackfolio.application;

import com.scherer.trackfolio.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    // List active applications for a user (soft-deletes excluded), newest first by applied date then created
    List<Application> findByOwnerAndDeletedAtIsNullOrderByAppliedAtDescCreatedAtDesc(User owner);

    // Simple filtered list by status (e.g., APPLIED, INTERVIEW, OFFER, REJECTED, WITHDRAWN)
    List<Application> findByOwnerAndStatusAndDeletedAtIsNullOrderByAppliedAtDescCreatedAtDesc(
            User owner, String status
    );

    // Date range helper (optional, useful later for analytics/pages)
    List<Application> findByOwnerAndAppliedAtBetweenAndDeletedAtIsNullOrderByAppliedAtDesc(
            User owner, LocalDate start, LocalDate end
    );
}
