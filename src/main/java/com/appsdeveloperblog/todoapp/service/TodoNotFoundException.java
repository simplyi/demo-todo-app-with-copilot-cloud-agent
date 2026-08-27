package com.appsdeveloperblog.todoapp.service;

public class TodoNotFoundException extends RuntimeException {

	public TodoNotFoundException(final String message) {
		super(message);
	}

}
