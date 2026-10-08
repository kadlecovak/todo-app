package com.example.todo_app;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<TodoEntry> getTodos(Boolean completed, Priority priority, String sortBy) {
        Sort sort;

        switch (sortBy != null ? sortBy : "") {
            case "dueDate":
                sort = Sort.by(Sort.Direction.ASC, "dueDate");
                break;
            case "priority":
                sort = Sort.by(Sort.Direction.ASC, "priority");
                break;
            case "priority-date":
                sort = Sort.by(Sort.Direction.ASC, "priority")
                        .and(Sort.by(Sort.Direction.ASC, "dueDate"));
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, "id");
                break;
        }

        if (completed != null && priority != null) {
            return repository.findByCompletedAndPriority(completed, priority, sort);
        } else if (completed != null) {
            return repository.findByCompleted(completed, sort);
        } else if (priority != null) {
            return repository.findByPriority(priority, sort);
        } else {
            return repository.findAll(sort);
        }
    }

    public TodoEntry createTodo(TodoEntry todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (todo.getDueDate() != null && todo.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        return repository.save(todo);
    }

    public TodoEntry updateTodo(Long id, TodoEntry updatedTodo) {
        return repository.findById(id).map(existingTodo -> {
            existingTodo.setTitle(updatedTodo.getTitle());
            existingTodo.setDescription(updatedTodo.getDescription());
            existingTodo.setCompleted(updatedTodo.isCompleted());
            return repository.save(existingTodo);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " was not found"));
    }

    public void deleteTodo(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Task with ID " + id + " was not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteCompletedTodos() {
        repository.deleteByCompleted(true);
    }
}