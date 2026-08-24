package com.appsdeveloperblog.todoapp.repository;

import com.appsdeveloperblog.todoapp.model.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	boolean existsByEmailAddress(String emailAddress);

	Optional<UserAccount> findByEmailAddress(String emailAddress);

}
