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
        TodoRequestDTO requestDto = new TodoRequestDTO();
        requestDto.setTitle("Title");
        requestDto.setDescription("");
        requestDto.setPriority(Priority.HIGH);
        requestDto.setDueDate(LocalDate.now().plusDays(1));

        TodoEntry savedEntity = new TodoEntry("Title", "", Priority.HIGH, LocalDate.now().plusDays(1));
        savedEntity.setId(1L);

        when(repository.save(any(TodoEntry.class))).thenReturn(savedEntity);
        TodoResponseDTO responseDto = service.createTodo(requestDto);

        assertNotNull(responseDto);
        assertEquals("Title", responseDto.getTitle());
        assertEquals(1L, responseDto.getId());

        verify(repository, times(1)).save(any(TodoEntry.class));
    }

    @Test
    void testCreateTodoException_EmptyTitle() {
        TodoRequestDTO badRequestDto = new TodoRequestDTO();
        badRequestDto.setTitle("");
        badRequestDto.setDescription("Description");
        badRequestDto.setDueDate(LocalDate.now().plusDays(1));
        badRequestDto.setPriority(Priority.HIGH);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createTodo(badRequestDto);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void testCreateTodoException_PastDate() {
        TodoRequestDTO badRequestDto = new TodoRequestDTO();
        badRequestDto.setTitle("Valid Title");
        badRequestDto.setDescription("Description");
        badRequestDto.setDueDate(LocalDate.now().minusDays(1));
        badRequestDto.setPriority(Priority.HIGH);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createTodo(badRequestDto);
        });

        verify(repository, never()).save(any());
    }
}