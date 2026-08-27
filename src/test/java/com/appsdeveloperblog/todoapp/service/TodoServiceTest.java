package com.appsdeveloperblog.todoapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appsdeveloperblog.todoapp.dto.TodoRequest;
import com.appsdeveloperblog.todoapp.dto.TodoResponse;
import com.appsdeveloperblog.todoapp.model.Todo;
import com.appsdeveloperblog.todoapp.model.UserAccount;
import com.appsdeveloperblog.todoapp.repository.TodoRepository;
import com.appsdeveloperblog.todoapp.repository.UserAccountRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

	@Mock
	private TodoRepository todoRepository;

	@Mock
	private UserAccountRepository userAccountRepository;

	@InjectMocks
	private TodoService todoService;

	private UserAccount user;

	@BeforeEach
	void setUp() {
		user = new UserAccount("Jane", "Doe", "jane@example.com", "encodedPassword");
	}

	@Test
	void getTodosForUserReturnsEmptyListWhenNoneExist() {
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		when(todoRepository.findByUserAccountId(user.getId())).thenReturn(List.of());

		final List<TodoResponse> result = todoService.getTodosForUser("jane@example.com");

		assertThat(result).isEmpty();
	}

	@Test
	void createTodoReturnsSavedTodo() {
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		final Todo saved = new Todo("Buy milk", user);
		when(todoRepository.save(any(Todo.class))).thenReturn(saved);

		final TodoRequest request = new TodoRequest();
		request.setTitle("Buy milk");

		final TodoResponse response = todoService.createTodo("jane@example.com", request);

		assertThat(response.title()).isEqualTo("Buy milk");
		assertThat(response.completed()).isFalse();
	}

	@Test
	void createTodoWithDueDateStoresDueDate() {
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		final Todo saved = new Todo("Doctor visit", user);
		saved.setDueDate(LocalDate.of(2025, 6, 15));
		when(todoRepository.save(any(Todo.class))).thenReturn(saved);

		final TodoRequest request = new TodoRequest();
		request.setTitle("Doctor visit");
		request.setDueDate(LocalDate.of(2025, 6, 15));

		final TodoResponse response = todoService.createTodo("jane@example.com", request);

		assertThat(response.dueDate()).isEqualTo(LocalDate.of(2025, 6, 15));
	}

	@Test
	void updateTodoModifiesFields() {
		final Todo existing = new Todo("Old title", user);
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		when(todoRepository.findByIdAndUserAccountId(1L, user.getId())).thenReturn(Optional.of(existing));
		when(todoRepository.save(existing)).thenReturn(existing);

		final TodoRequest request = new TodoRequest();
		request.setTitle("New title");
		request.setCompleted(true);

		final TodoResponse response = todoService.updateTodo("jane@example.com", 1L, request);

		assertThat(response.title()).isEqualTo("New title");
		assertThat(response.completed()).isTrue();
	}

	@Test
	void updateTodoThrowsWhenNotFound() {
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		when(todoRepository.findByIdAndUserAccountId(99L, user.getId())).thenReturn(Optional.empty());

		final TodoRequest request = new TodoRequest();
		request.setTitle("Whatever");

		assertThatThrownBy(() -> todoService.updateTodo("jane@example.com", 99L, request))
				.isInstanceOf(TodoNotFoundException.class);
	}

	@Test
	void deleteTodoRemovesEntity() {
		final Todo existing = new Todo("To delete", user);
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		when(todoRepository.findByIdAndUserAccountId(1L, user.getId())).thenReturn(Optional.of(existing));

		todoService.deleteTodo("jane@example.com", 1L);

		verify(todoRepository).delete(existing);
	}

	@Test
	void deleteTodoThrowsWhenNotFound() {
		when(userAccountRepository.findByEmailAddress("jane@example.com")).thenReturn(Optional.of(user));
		when(todoRepository.findByIdAndUserAccountId(99L, user.getId())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> todoService.deleteTodo("jane@example.com", 99L))
				.isInstanceOf(TodoNotFoundException.class);
	}

}
