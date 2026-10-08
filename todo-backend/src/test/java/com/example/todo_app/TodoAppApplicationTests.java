package com.example.todo_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Tyto dva importy musíme přidat ručně pro assertEquals a assertNotNull
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TodoAppApplicationTests {

	@Autowired
	private TodoController controller;

	@Test
	void testCreateTodoItem() {
	}
}