package com.appsdeveloperblog.todoapp.config;

import com.appsdeveloperblog.todoapp.service.UserAccountDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final UserAccountDetailsService userAccountDetailsService;
	private final PasswordEncoder passwordEncoder;

	public SecurityConfiguration(final UserAccountDetailsService userAccountDetailsService,
			final PasswordEncoder passwordEncoder) {
		this.userAccountDetailsService = userAccountDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userAccountDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);

		http
			.authenticationProvider(authProvider)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/register", "/login").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.usernameParameter("emailAddress")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login?error")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			);
		return http.build();
	}

}
