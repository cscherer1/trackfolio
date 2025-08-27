package com.scherer.trackfolio.web;

import com.scherer.trackfolio.user.User;
import com.scherer.trackfolio.user.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class SignupController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public SignupController(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    // Show the signup form
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupForm());
        return "signup";
    }

    // Handle signup submission
    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute("form") SignupForm form,
                               BindingResult binding,
                               Model model) {

        // unique email check
        users.findByEmail(form.email()).ifPresent(u ->
                binding.addError(new FieldError("form", "email", "Email is already registered"))
        );

        // password confirm check
        if (!form.password().equals(form.confirmPassword())) {
            binding.addError(new FieldError("form", "confirmPassword", "Passwords do not match"));
        }

        if (binding.hasErrors()) {
            return "signup";
        }

        var now = LocalDateTime.now();
        User u = User.builder()
                .email(form.email().trim().toLowerCase())
                .password(passwordEncoder.encode(form.password()))
                .displayName(form.displayName())
                .role("ROLE_USER")
                .createdAt(now)
                .updatedAt(now)
                .build();

        users.save(u);

        // after signup, send them to login
        model.addAttribute("signupSuccess", true);
        return "redirect:/login";
    }

    // simple DTO with validation
    public static class SignupForm {
        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank
        private String confirmPassword;

        @Size(max = 100)
        private String displayName;

        // getters/setters (records don't play great with binding on older setups)
        public String email() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String password() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String confirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
        public String displayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }
}
