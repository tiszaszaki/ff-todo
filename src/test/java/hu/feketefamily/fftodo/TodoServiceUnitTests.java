package hu.feketefamily.fftodo;

import hu.feketefamily.fftodo.model.repository.TodoRepository;
import hu.feketefamily.fftodo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class TodoServiceUnitTests {

	private Long TEST_TODO_ID = 1L;

	@InjectMocks
	private TodoService todoService;

	@Mock
	private TodoRepository todoRepository;

	@Test
	void shouldRemoveExistingTodo() {
		when(todoRepository.existsById(TEST_TODO_ID)).thenReturn(true);
		doNothing().when(todoRepository).deleteById(TEST_TODO_ID);

		todoService.removeTodo(TEST_TODO_ID);

		verify(todoRepository).deleteById(TEST_TODO_ID);
	}

	@Test
	void shouldNotRemoveNonExistingTodo() {
		when(todoRepository.existsById(TEST_TODO_ID)).thenReturn(false);

		todoService.removeTodo(TEST_TODO_ID);

		verify(todoRepository, times(0)).deleteById(TEST_TODO_ID);
	}
}
