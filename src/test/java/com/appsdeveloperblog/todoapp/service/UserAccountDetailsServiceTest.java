package com.appsdeveloperblog.todoapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.appsdeveloperblog.todoapp.model.UserAccount;
import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserAccountDetailsServiceTest {

	@Mock
	private UserAccountRepository userAccountRepository;

	@InjectMocks
	private UserAccountDetailsService userAccountDetailsService;

	@Test
	void loadUserByUsernameReturnsUserDetailsForKnownEmail() {
		final UserAccount account = new UserAccount("Taylor", "Jones", "taylor@example.com", "encodedPassword");
		when(userAccountRepository.findByEmailAddress("taylor@example.com")).thenReturn(Optional.of(account));

		final UserDetails userDetails = userAccountDetailsService.loadUserByUsername("taylor@example.com");

		assertThat(userDetails.getUsername()).isEqualTo("taylor@example.com");
		assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
		assertThat(userDetails.getAuthorities()).hasSize(1);
		assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
	}

	@Test
	void loadUserByUsernameThrowsForUnknownEmail() {
		when(userAccountRepository.findByEmailAddress("unknown@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userAccountDetailsService.loadUserByUsername("unknown@example.com"))
				.isInstanceOf(UsernameNotFoundException.class);
	}

}
