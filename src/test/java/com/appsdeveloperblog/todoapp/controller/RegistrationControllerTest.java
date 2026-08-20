package com.appsdeveloperblog.todoapp.controller;

import com.appsdeveloperblog.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void showRegistrationForm_returnsRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void registerUser_success_savesEncodedPasswordAndRedirects() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("email", "jane@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?success"));

        var saved = userRepository.findByEmail("jane@example.com");
        assertThat(saved).isPresent();
        assertThat(saved.get().getPassword()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", saved.get().getPassword())).isTrue();
    }

    @Test
    void registerUser_passwordMismatch_returnsFormWithError() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("email", "jane@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user", "confirmPassword"));

        assertThat(userRepository.findByEmail("jane@example.com")).isEmpty();
    }

    @Test
    void registerUser_missingRequiredFields_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user",
                        "firstName", "lastName", "email", "password", "confirmPassword"));
    }

    @Test
    void registerUser_duplicateEmail_returnsFormWithError() throws Exception {
        // Register first user
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("email", "jane@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection());

        // Try to register again with same email
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "jane@example.com")
                        .param("password", "password456")
                        .param("confirmPassword", "password456"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void registerUser_invalidEmail_returnsFormWithError() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("email", "not-an-email")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }
}
