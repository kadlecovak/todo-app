package com.example.todo_app;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<TodoResponseDTO> getTodos(Boolean completed, Priority priority, String sortBy) {

        Sort sort = Sort.by(Sort.Direction.ASC, "completed");
        
        switch (sortBy != null ? sortBy : "") {
            case "dueDate":
                sort = sort.and(Sort.by(Sort.Order.asc("dueDate").nullsLast()));
                break;
            case "priority":
                sort = sort.and(Sort.by(Sort.Direction.ASC, "priority"));
                break;
            case "priority-date":
                sort = sort.and(Sort.by(Sort.Direction.ASC, "priority"))
                        .and(Sort.by(Sort.Order.asc("dueDate").nullsLast()));
                break;
            default:
                sort = sort.and(Sort.by(Sort.Direction.DESC, "id"));
                break;
        }

        List<TodoEntry> entries;
        if (completed != null && priority != null) {
            entries = repository.findByCompletedAndPriority(completed, priority, sort);
        } else if (completed != null) {
            entries = repository.findByCompleted(completed, sort);
        } else if (priority != null) {
            entries = repository.findByPriority(priority, sort);
        } else {
            entries = repository.findAll(sort);
        }

        return entries.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public TodoResponseDTO createTodo(TodoRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }

        TodoEntry todo = new TodoEntry(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate()
        );

        TodoEntry saved = repository.save(todo);
        return mapToDTO(saved);
    }

    public Optional<TodoResponseDTO> updateTodo(Long id, TodoRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        return repository.findById(id).map(existingTodo -> {
            existingTodo.setTitle(request.getTitle());
            existingTodo.setDescription(request.getDescription());
            existingTodo.setCompleted(request.isCompleted());
            existingTodo.setPriority(request.getPriority());
            existingTodo.setDueDate(request.getDueDate());

            TodoEntry updated = repository.save(existingTodo);
            return mapToDTO(updated);
        });
    }

    public boolean deleteTodo(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteCompletedTodos() {
        repository.deleteByCompleted(true);
    }

    private TodoResponseDTO mapToDTO(TodoEntry todo) {
        return new TodoResponseDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.getDueDate(),
                todo.isCompleted()
        );
    }
}