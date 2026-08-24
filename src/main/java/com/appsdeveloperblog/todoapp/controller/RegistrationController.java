package com.appsdeveloperblog.todoapp.controller;

import com.appsdeveloperblog.todoapp.dto.RegistrationForm;
import com.appsdeveloperblog.todoapp.service.DuplicateEmailException;
import com.appsdeveloperblog.todoapp.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

	private final UserRegistrationService userRegistrationService;

	public RegistrationController(final UserRegistrationService userRegistrationService) {
		this.userRegistrationService = userRegistrationService;
	}

	@GetMapping("/register")
	public String showRegistrationForm(final Model model) {
		if (!model.containsAttribute("registrationForm")) {
			model.addAttribute("registrationForm", new RegistrationForm());
		}
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("registrationForm") final RegistrationForm registrationForm,
			final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
		validatePasswordConfirmation(registrationForm, bindingResult);
		if (bindingResult.hasErrors()) {
			return "register";
		}

		try {
			userRegistrationService.registerUser(registrationForm);
		}
		catch (final DuplicateEmailException exception) {
			bindingResult.rejectValue("emailAddress", "registration.emailAddress.duplicate",
					exception.getMessage());
			return "register";
		}

		redirectAttributes.addFlashAttribute("registrationSuccess", true);
		return "redirect:/register";
	}

	private void validatePasswordConfirmation(final RegistrationForm registrationForm, final BindingResult bindingResult) {
		if (!bindingResult.hasFieldErrors("password") && !bindingResult.hasFieldErrors("passwordConfirmation")
				&& !registrationForm.getPassword().equals(registrationForm.getPasswordConfirmation())) {
			bindingResult.rejectValue("passwordConfirmation", "registration.passwordConfirmation.mismatch",
					"Password confirmation must match password");
		}
	}

}
