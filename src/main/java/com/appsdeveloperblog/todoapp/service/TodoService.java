package com.appsdeveloperblog.todoapp.service;

import com.appsdeveloperblog.todoapp.dto.TodoRequest;
import com.appsdeveloperblog.todoapp.dto.TodoResponse;
import com.appsdeveloperblog.todoapp.model.Todo;
import com.appsdeveloperblog.todoapp.model.UserAccount;
import com.appsdeveloperblog.todoapp.repository.TodoRepository;
import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

	private final TodoRepository todoRepository;
	private final UserAccountRepository userAccountRepository;

	public TodoService(final TodoRepository todoRepository, final UserAccountRepository userAccountRepository) {
		this.todoRepository = todoRepository;
		this.userAccountRepository = userAccountRepository;
	}

	@Transactional(readOnly = true)
	public List<TodoResponse> getTodosForUser(final String emailAddress) {
		final UserAccount user = findUser(emailAddress);
		return todoRepository.findByUserAccountId(user.getId()).stream()
				.map(TodoService::toResponse)
				.toList();
	}

	@Transactional
	public TodoResponse createTodo(final String emailAddress, final TodoRequest request) {
		final UserAccount user = findUser(emailAddress);
		final Todo todo = new Todo(request.getTitle().trim(), user);
		todo.setCompleted(request.isCompleted());
		todo.setDueDate(request.getDueDate());
		return toResponse(todoRepository.save(todo));
	}

	@Transactional
	public TodoResponse updateTodo(final String emailAddress, final Long todoId, final TodoRequest request) {
		final Todo todo = findTodoForUser(emailAddress, todoId);
		todo.setTitle(request.getTitle().trim());
		todo.setCompleted(request.isCompleted());
		todo.setDueDate(request.getDueDate());
		return toResponse(todoRepository.save(todo));
	}

	@Transactional
	public void deleteTodo(final String emailAddress, final Long todoId) {
		final Todo todo = findTodoForUser(emailAddress, todoId);
		todoRepository.delete(todo);
	}

	private UserAccount findUser(final String emailAddress) {
		return userAccountRepository.findByEmailAddress(emailAddress)
				.orElseThrow(() -> new UsernameNotFoundException("Authentication principal not found"));
	}

	private Todo findTodoForUser(final String emailAddress, final Long todoId) {
		final UserAccount user = findUser(emailAddress);
		return todoRepository.findByIdAndUserAccountId(todoId, user.getId())
				.orElseThrow(() -> new TodoNotFoundException("Todo not found: " + todoId));
	}

	private static TodoResponse toResponse(final Todo todo) {
		return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getDueDate());
	}

}
