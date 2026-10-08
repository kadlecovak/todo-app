package com.example.todo_app;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class TodoEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Priority priority = Priority.MEDIUM;
    private LocalDate dueDate;
    private boolean completed;

    public TodoEntry() {
    }

    public TodoEntry(String title, String description, Priority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority;  }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
