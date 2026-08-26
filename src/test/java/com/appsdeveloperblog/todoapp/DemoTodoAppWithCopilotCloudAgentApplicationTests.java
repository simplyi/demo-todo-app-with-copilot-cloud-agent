package com.appsdeveloperblog.todoapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appsdeveloperblog.todoapp.model.UserAccount;
import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DemoTodoAppWithCopilotCloudAgentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void registrationPageDisplaysExpectedFields() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First name")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last name")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Email address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password confirmation")));
	}

	@Test
	void successfulRegistrationCreatesEncodedUserAccount() throws Exception {
		userAccountRepository.deleteAll();

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "Taylor")
				.param("lastName", "Jones")
				.param("emailAddress", "taylor@example.com")
				.param("password", "Password123!")
				.param("passwordConfirmation", "Password123!"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register"));

		final UserAccount savedUser = userAccountRepository.findByEmailAddress("taylor@example.com").orElseThrow();
		final String storedPasswordHash = (String) ReflectionTestUtils.getField(savedUser, "password");
		assertThat(savedUser.getFirstName()).isEqualTo("Taylor");
		assertThat(savedUser.getLastName()).isEqualTo("Jones");
		assertThat(storedPasswordHash).isNotEqualTo("Password123!");
		assertThat(passwordEncoder.matches("Password123!", storedPasswordHash)).isTrue();
	}

	@Test
	void registrationRejectsMismatchedPasswords() throws Exception {
		userAccountRepository.deleteAll();

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "Taylor")
				.param("lastName", "Jones")
				.param("emailAddress", "taylor@example.com")
				.param("password", "Password123!")
				.param("passwordConfirmation", "DifferentPassword123!"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password confirmation must match password")));

		assertThat(userAccountRepository.count()).isZero();
	}

	@Test
	void registrationRejectsMissingAndInvalidFields() throws Exception {
		userAccountRepository.deleteAll();

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "")
				.param("lastName", "")
				.param("emailAddress", "invalid-email")
				.param("password", "")
				.param("passwordConfirmation", ""))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First name is required")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last name is required")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Email address must be valid")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password is required")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password confirmation is required")));

		assertThat(userAccountRepository.count()).isZero();
	}

	@Test
	void registrationRejectsDuplicateEmailAddress() throws Exception {
		userAccountRepository.deleteAll();

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "Taylor")
				.param("lastName", "Jones")
				.param("emailAddress", "taylor@example.com")
				.param("password", "Password123!")
				.param("passwordConfirmation", "Password123!"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "Jordan")
				.param("lastName", "Smith")
				.param("emailAddress", "TAYLOR@example.com")
				.param("password", "Password123!")
				.param("passwordConfirmation", "Password123!"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("An account with this email address already exists")));

		assertThat(userAccountRepository.count()).isEqualTo(1);
	}

	@Test
	void loginPageIsAccessibleWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Email address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Password")));
	}

	@Test
	void unauthenticatedAccessToProtectedRouteRedirectsToLogin() throws Exception {
		mockMvc.perform(get("/some-protected-page"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void loginWithValidCredentialsRedirectsToHome() throws Exception {
		userAccountRepository.deleteAll();

		mockMvc.perform(post("/register").with(csrf())
				.param("firstName", "Alex")
				.param("lastName", "Brown")
				.param("emailAddress", "alex@example.com")
				.param("password", "Password123!")
				.param("passwordConfirmation", "Password123!"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(post("/login").with(csrf())
				.param("emailAddress", "alex@example.com")
				.param("password", "Password123!"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void loginWithInvalidCredentialsRedirectsToLoginWithError() throws Exception {
		mockMvc.perform(post("/login").with(csrf())
				.param("emailAddress", "nobody@example.com")
				.param("password", "wrongpassword"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));
	}

}
