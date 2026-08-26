package com.appsdeveloperblog.todoapp.service;

import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;

	public UserAccountDetailsService(final UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userAccountRepository.findByEmailAddress(username)
				.map(account -> new User(account.getEmailAddress(), account.getPassword(),
						List.of(new SimpleGrantedAuthority("ROLE_USER"))))
				.orElseThrow(() -> new UsernameNotFoundException("No account found for email: " + username));
	}

}
