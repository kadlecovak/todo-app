package com.example.todo_app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoAppApplicationTests {

    @Mock
    private TodoRepository repository;

    @InjectMocks
    private TodoService service;

    @Test
    void testCreateTodo() {
        TodoEntry newTodo = new TodoEntry("Title", "", Priority.HIGH, LocalDate.now().plusDays(1));

        when(repository.save(any(TodoEntry.class))).thenReturn(newTodo);

        TodoEntry savedTodo = service.createTodo(newTodo);

        assertNotNull(savedTodo);
        assertEquals("Title", savedTodo.getTitle());
        verify(repository, times(1)).save(newTodo);
    }

    @Test
    void testCreateTodoException() {
        TodoEntry badTodo = new TodoEntry("", "Title", Priority.HIGH, null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createTodo(badTodo);
        });
        verify(repository, never()).save(any());
    }
}