package com.scherer.trackfolio.application;

import com.scherer.trackfolio.user.User;
import com.scherer.trackfolio.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/applications")
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

    // List all applications for current user
    @GetMapping
    public String list(Authentication auth, Model model) {
        User owner = getCurrentUser(auth);
        List<Application> list = applications.findByOwnerAndDeletedAtIsNullOrderByAppliedAtDescCreatedAtDesc(owner);
        model.addAttribute("applications", list);
        return "applications/list"; // templates/applications/list.html
    }

    // Show create form
    @GetMapping("/new")
    public String newForm(Model model) {
        Application app = Application.builder()
                .appliedAt(LocalDate.now())
                .status("APPLIED")
                .source("LINKEDIN")
                .build();
        model.addAttribute("application", app);
        return "applications/form"; // templates/applications/form.html
    }

    // Handle create
    @PostMapping
    public String create(Authentication auth, @ModelAttribute("application") Application app) {
        User owner = getCurrentUser(auth);
        app.setOwner(owner);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        applications.save(app);
        return "redirect:/applications";
    }
}
