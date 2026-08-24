package com.appsdeveloperblog.todoapp.service;

public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException(final String message) {
		super(message);
	}

}
