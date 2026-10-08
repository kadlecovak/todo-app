package com.example.todo_app;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntry, Long> {

    void deleteByCompleted(boolean completed);
    List<TodoEntry> findByCompleted(boolean completed, Sort sort);
    List<TodoEntry> findByPriority(Priority priority, Sort sort);
    List<TodoEntry> findBydueDate(LocalDate dueDate, Sort sort);
    List<TodoEntry> findByCompletedAndPriority(boolean completed, Priority priority, Sort sort);
}
