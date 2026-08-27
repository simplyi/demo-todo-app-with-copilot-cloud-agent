package com.appsdeveloperblog.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class TodoRequest {

	@NotBlank(message = "Title is required")
	private String title;

	private boolean completed;

	private LocalDate dueDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(final boolean completed) {
		this.completed = completed;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(final LocalDate dueDate) {
		this.dueDate = dueDate;
	}

}
