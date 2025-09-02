package com.scherer.trackfolio.web;

import com.scherer.trackfolio.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LandingController {

    private final UserRepository users;

    @GetMapping("/")
    public String landing(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String email = auth.getName(); // our username == email
            users.findByEmail(email).ifPresent(u -> model.addAttribute("currentUser", u));
        }
        return "landing"; // templates/landing.html
    }
}
