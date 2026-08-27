package com.appsdeveloperblog.todoapp.repository;

import com.appsdeveloperblog.todoapp.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	List<Todo> findByUserAccountId(Long userAccountId);

	Optional<Todo> findByIdAndUserAccountId(Long id, Long userAccountId);

}
