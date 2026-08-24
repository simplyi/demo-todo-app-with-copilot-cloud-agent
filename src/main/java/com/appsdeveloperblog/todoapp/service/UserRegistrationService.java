package com.appsdeveloperblog.todoapp.service;

import com.appsdeveloperblog.todoapp.dto.RegistrationForm;
import com.appsdeveloperblog.todoapp.model.UserAccount;
import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;

	public UserRegistrationService(final UserAccountRepository userAccountRepository,
			final PasswordEncoder passwordEncoder) {
		this.userAccountRepository = userAccountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserAccount registerUser(final RegistrationForm registrationForm) {
		final String normalizedEmailAddress = normalizeEmailAddress(registrationForm.getEmailAddress());
		if (userAccountRepository.existsByEmailAddress(normalizedEmailAddress)) {
			throw new DuplicateEmailException("An account with this email address already exists");
		}

		final UserAccount userAccount = new UserAccount(registrationForm.getFirstName().trim(),
				registrationForm.getLastName().trim(), normalizedEmailAddress,
				passwordEncoder.encode(registrationForm.getPassword()));
		try {
			return userAccountRepository.save(userAccount);
		}
		catch (final org.springframework.dao.DataIntegrityViolationException exception) {
			if (userAccountRepository.existsByEmailAddress(normalizedEmailAddress)) {
				throw new DuplicateEmailException("An account with this email address already exists");
			}
			throw exception;
		}
	}

	private String normalizeEmailAddress(final String emailAddress) {
		return emailAddress.trim().toLowerCase(Locale.ROOT);
	}

}
