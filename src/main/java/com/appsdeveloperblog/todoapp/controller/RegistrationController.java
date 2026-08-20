package com.appsdeveloperblog.todoapp.controller;

import com.appsdeveloperblog.todoapp.dto.UserRegistrationDto;
import com.appsdeveloperblog.todoapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                               BindingResult bindingResult) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwords.mismatch",
                    "Passwords do not match");
        }

        if (userService.existsByEmail(dto.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "An account with that email address already exists");
        }

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.register(dto);
        return "redirect:/register?success";
    }
}
