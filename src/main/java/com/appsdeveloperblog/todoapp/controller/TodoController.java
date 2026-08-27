package com.appsdeveloperblog.todoapp.controller;

import com.appsdeveloperblog.todoapp.dto.TodoRequest;
import com.appsdeveloperblog.todoapp.dto.TodoResponse;
import com.appsdeveloperblog.todoapp.service.TodoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

	private final TodoService todoService;

	public TodoController(final TodoService todoService) {
		this.todoService = todoService;
	}

	@GetMapping
	public List<TodoResponse> getTodos(@AuthenticationPrincipal final UserDetails userDetails) {
		return todoService.getTodosForUser(userDetails.getUsername());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TodoResponse createTodo(@AuthenticationPrincipal final UserDetails userDetails,
			@Valid @RequestBody final TodoRequest request) {
		return todoService.createTodo(userDetails.getUsername(), request);
	}

	@PutMapping("/{id}")
	public TodoResponse updateTodo(@AuthenticationPrincipal final UserDetails userDetails,
			@PathVariable final Long id,
			@Valid @RequestBody final TodoRequest request) {
		return todoService.updateTodo(userDetails.getUsername(), id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTodo(@AuthenticationPrincipal final UserDetails userDetails,
			@PathVariable final Long id) {
		todoService.deleteTodo(userDetails.getUsername(), id);
	}

}
