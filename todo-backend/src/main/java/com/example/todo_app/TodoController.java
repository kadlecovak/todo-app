package com.example.todo_app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TodoEntry>> getTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String sortBy) {

        return ResponseEntity.ok(service.getTodos(completed, priority, sortBy));
    }

    @PostMapping
    public ResponseEntity<TodoEntry> createTodo(@RequestBody TodoEntry todo) {
        TodoEntry created = service.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoEntry> updateTodo(@PathVariable Long id, @RequestBody TodoEntry updatedTodo) {
        return ResponseEntity.ok(service.updateTodo(id, updatedTodo));
    }

    @DeleteMapping("/completed")
    public ResponseEntity<Void> deleteCompletedTodos() {
        service.deleteCompletedTodos();
        return ResponseEntity.noContent().build();
    }
}
