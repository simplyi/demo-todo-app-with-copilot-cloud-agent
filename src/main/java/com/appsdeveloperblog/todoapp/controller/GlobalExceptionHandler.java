package com.appsdeveloperblog.todoapp.controller;

import com.appsdeveloperblog.todoapp.service.TodoNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(TodoNotFoundException.class)
	public ResponseEntity<Void> handleTodoNotFound() {
		return ResponseEntity.notFound().build();
	}

}
