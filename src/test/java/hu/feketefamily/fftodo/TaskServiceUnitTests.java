package hu.feketefamily.fftodo;

import hu.feketefamily.fftodo.model.repository.TaskRepository;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceUnitTests {
	private static final Long TEST_TASK_ID = 1L;

	@InjectMocks
	private TaskService taskService;

	@Mock
	private TaskRepository taskRepository;

	@Test
	void shouldRemoveExistingTask() {
		when(taskRepository.existsById(TEST_TASK_ID)).thenReturn(true);
		doNothing().when(taskRepository).deleteById(TEST_TASK_ID);

		taskService.removeTask(TEST_TASK_ID);

		verify(taskRepository).deleteById(TEST_TASK_ID);
	}

	@Test
	void shouldNotRemoveNonExistingTask() {
		when(taskRepository.existsById(TEST_TASK_ID)).thenReturn(false);

		taskService.removeTask(TEST_TASK_ID);

		verify(taskRepository, times(0)).deleteById(TEST_TASK_ID);
	}
}
