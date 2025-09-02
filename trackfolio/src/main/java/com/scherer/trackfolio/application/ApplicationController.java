package com.scherer.trackfolio.application;

import com.scherer.trackfolio.user.User;
import com.scherer.trackfolio.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/applications") // JSON API for Angular
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository applications;
    private final UserRepository users;

    private User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Not logged in");
        }
        return users.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    // GET /api/applications  -> JSON array for the current user
    @GetMapping(produces = "application/json")
    public List<Application> list(Authentication auth) {
        User owner = getCurrentUser(auth);
        return applications.findByOwnerAndDeletedAtIsNullOrderByAppliedAtDescCreatedAtDesc(owner);
    }

    // POST /api/applications -> create one (JSON in/out)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Application> create(@RequestBody Application in, Authentication auth) {
        User owner = getCurrentUser(auth);

        // server-side ownership + sane defaults
        in.setId(null);
        in.setOwner(owner);
        if (in.getAppliedAt() == null) in.setAppliedAt(LocalDate.now());
        if (in.getStatus() == null)    in.setStatus("APPLIED");
        if (in.getSource() == null)    in.setSource("LINKEDIN");
        in.setCreatedAt(LocalDateTime.now());
        in.setUpdatedAt(LocalDateTime.now());

        Application saved = applications.save(in);
        return ResponseEntity.created(URI.create("/api/applications/" + saved.getId()))
                .body(saved);
    }
}
