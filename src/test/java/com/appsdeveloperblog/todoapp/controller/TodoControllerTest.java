package com.appsdeveloperblog.todoapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appsdeveloperblog.todoapp.dto.TodoRequest;
import com.appsdeveloperblog.todoapp.dto.TodoResponse;
import com.appsdeveloperblog.todoapp.service.TodoNotFoundException;
import com.appsdeveloperblog.todoapp.service.TodoService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

	private static final String USER_EMAIL = "jane@example.com";

	@Mock
	private TodoService todoService;

	@InjectMocks
	private TodoController todoController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(todoController)
				.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();

		final User mockUser = new User(USER_EMAIL, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
		final UsernamePasswordAuthenticationToken authentication =
				UsernamePasswordAuthenticationToken.authenticated(mockUser, null, mockUser.getAuthorities());
		final var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void getTodosReturnsListForAuthenticatedUser() throws Exception {
		final TodoResponse todo = new TodoResponse(1L, "Buy milk", false, null);
		when(todoService.getTodosForUser(USER_EMAIL)).thenReturn(List.of(todo));

		mockMvc.perform(get("/api/todos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Buy milk"))
				.andExpect(jsonPath("$[0].completed").value(false));
	}

	@Test
	void getTodosReturnsEmptyListWhenNoneExist() throws Exception {
		when(todoService.getTodosForUser(USER_EMAIL)).thenReturn(List.of());

		mockMvc.perform(get("/api/todos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void createTodoReturnsCreatedStatus() throws Exception {
		final TodoResponse created = new TodoResponse(2L, "Read book", false, LocalDate.of(2025, 8, 1));
		when(todoService.createTodo(eq(USER_EMAIL), any(TodoRequest.class))).thenReturn(created);

		mockMvc.perform(post("/api/todos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Read book\",\"dueDate\":\"2025-08-01\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.title").value("Read book"))
				.andExpect(jsonPath("$.dueDate").value("2025-08-01"));
	}

	@Test
	void createTodoReturnsBadRequestWhenTitleIsBlank() throws Exception {
		mockMvc.perform(post("/api/todos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateTodoReturnsUpdatedTodo() throws Exception {
		final TodoResponse updated = new TodoResponse(1L, "Updated title", true, null);
		when(todoService.updateTodo(eq(USER_EMAIL), eq(1L), any(TodoRequest.class))).thenReturn(updated);

		mockMvc.perform(put("/api/todos/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Updated title\",\"completed\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated title"))
				.andExpect(jsonPath("$.completed").value(true));
	}

	@Test
	void updateTodoReturnsNotFoundWhenTodoDoesNotExist() throws Exception {
		when(todoService.updateTodo(eq(USER_EMAIL), eq(99L), any(TodoRequest.class)))
				.thenThrow(new TodoNotFoundException("Todo not found: 99"));

		mockMvc.perform(put("/api/todos/99")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Whatever\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTodoReturnsNoContent() throws Exception {
		doNothing().when(todoService).deleteTodo(USER_EMAIL, 1L);

		mockMvc.perform(delete("/api/todos/1"))
				.andExpect(status().isNoContent());

		verify(todoService).deleteTodo(USER_EMAIL, 1L);
	}

	@Test
	void deleteTodoReturnsNotFoundWhenTodoDoesNotExist() throws Exception {
		doThrow(new TodoNotFoundException("Todo not found: 99")).when(todoService).deleteTodo(USER_EMAIL, 99L);

		mockMvc.perform(delete("/api/todos/99"))
				.andExpect(status().isNotFound());
	}

}



