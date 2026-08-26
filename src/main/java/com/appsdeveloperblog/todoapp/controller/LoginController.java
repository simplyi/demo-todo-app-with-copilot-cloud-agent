package com.appsdeveloperblog.todoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String showLoginForm(
			@RequestParam(value = "error", required = false) final String error,
			@RequestParam(value = "logout", required = false) final String logout,
			final Model model) {
		if (error != null) {
			model.addAttribute("loginError", true);
		}
		if (logout != null) {
			model.addAttribute("logoutSuccess", true);
		}
		return "login";
	}

}
