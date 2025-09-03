package com.scherer.trackfolio.application;

import com.scherer.trackfolio.user.User;
import com.scherer.trackfolio.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication auth) {
        var owner = users.findByEmail(auth.getName()).orElseThrow();
        var app = applications.findById(id)
                .filter(a -> a.getOwner().getId().equals(owner.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        app.setDeletedAt(LocalDateTime.now());
        applications.save(app);
        return ResponseEntity.noContent().build();
    }

    // Get single application
    @GetMapping("/{id}")
    public ResponseEntity<Application> find(@PathVariable UUID id, Authentication auth) {
        User owner = getCurrentUser(auth);
        return applications.findById(id)
                .filter(app -> app.getOwner().equals(owner))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Application> update(@PathVariable UUID id,
                                              @RequestBody Application in,
                                              Authentication auth) {
        User owner = getCurrentUser(auth);

        return applications.findById(id)
                .filter(app -> app.getOwner().equals(owner))
                .map(existing -> {
                    existing.setCompany(in.getCompany());
                    existing.setRoleTitle(in.getRoleTitle());
                    existing.setSource(in.getSource());
                    existing.setStatus(in.getStatus());
                    existing.setAppliedAt(in.getAppliedAt());
                    existing.setLocation(in.getLocation());
                    existing.setSalaryText(in.getSalaryText());
                    existing.setJobLink(in.getJobLink());
                    existing.setNotes(in.getNotes());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(applications.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
