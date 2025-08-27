package com.scherer.trackfolio.web;

import com.scherer.trackfolio.user.User;
import com.scherer.trackfolio.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository users;

    public ProfileController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        String email = auth.getName(); // Spring Security username = our email
        User u = users.findByEmail(email).orElseThrow(); // should exist
        model.addAttribute("user", u);
        return "profile"; // -> src/main/resources/templates/profile.html
    }
}
