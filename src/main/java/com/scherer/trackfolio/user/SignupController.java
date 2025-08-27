package com.scherer.trackfolio.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
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

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute("form") SignupForm form,
                               BindingResult binding,
                               Model model) {

        // unique email check
        users.findByEmail(form.getEmail()).ifPresent(u ->
                binding.addError(new FieldError("form", "email", "Email is already registered"))
        );

        // password confirm check
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            binding.addError(new FieldError("form", "confirmPassword", "Passwords do not match"));
        }

        if (binding.hasErrors()) {
            return "signup";
        }

        var now = LocalDateTime.now();
        User u = User.builder()
                .email(form.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(form.getPassword()))
                .displayName(form.getDisplayName())
                .role("ROLE_USER")
                .createdAt(now)
                .updatedAt(now)
                .build();

        users.save(u);

        return "redirect:/login";
    }

    // DTO for form binding
    @Getter
    @Setter
    public static class SignupForm {
        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank
        private String confirmPassword;

        @Size(max = 100)
        private String displayName;
    }
}
