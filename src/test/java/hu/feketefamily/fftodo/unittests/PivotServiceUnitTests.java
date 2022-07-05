package hu.feketefamily.fftodo.unittests;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.model.repository.BoardRepository;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import hu.feketefamily.fftodo.model.repository.TodoRepository;
import hu.feketefamily.fftodo.pivot.PivotResponse;
import hu.feketefamily.fftodo.pivot.PivotService;
import hu.feketefamily.fftodo.pivot.ReadinessRecord;
import hu.feketefamily.fftodo.service.BoardService;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static hu.feketefamily.fftodo.constants.ErrorMessages.BOARD_NOT_EXIST_MESSAGE;
import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;
import static hu.feketefamily.fftodo.constants.ErrorMessages.TASK_NOT_EXIST_MESSAGE;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PivotServiceUnitTests {

	@InjectMocks
	private BoardService boardService;
	@InjectMocks
	private TodoService todoService;
	@InjectMocks
	private TaskService taskService;
	@InjectMocks
	private PivotService pivotService;

	@Mock
	private BoardRepository boardRepository;
	@Mock
	private TodoRepository todoRepository;
	@Mock
	private TaskRepository taskRepository;

	private static final Long TEST_BOARD_ID = 0L;
	private static final Board TEST_BOARD =
		Board.builder()
			.id(TEST_BOARD_ID)
			.name("Test board")
			.description("Test description")
			.author("Test author")
			.dateCreated(new Date())
			.readonlyTodos(false)
			.readonlyTasks(false)
			.build();

	private static final Long TEST_TODO_ID = 0L;
	private static final Todo TEST_TODO =
		Todo.builder()
			.id(TEST_TODO_ID)
			.name("Test todo")
			.description("Test description")
			.phase(TodoCommon.todoPhaseMin)
			.dateCreated(new Date())
			.dateModified(new Date())
			.board(TEST_BOARD)
			.build();

	private static final Long TEST_TASK_ID = 0L;
	private static final Task TEST_TASK =
		Task.builder()
			.id(TEST_TASK_ID)
			.name("Test task")
			.done(true)
			.todo(TEST_TODO)
			.build();

	@Test
	void shouldFetchBoardReadiness() {
		/*
		var testResponse = new PivotResponse<ReadinessRecord>();

		when(boardRepository.existsById(TEST_BOARD_ID)).thenReturn(true);
		when(boardRepository.findById(TEST_BOARD_ID)
			.orElseThrow(() -> new NotExistException(BOARD_NOT_EXIST_MESSAGE(TEST_BOARD_ID, TEST_BOARD.getName())))).thenReturn(TEST_BOARD);

		when(todoRepository.existsById(TEST_TODO_ID)).thenReturn(true);
		when(todoRepository.findById(TEST_TODO_ID)
			.orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE(TEST_TODO_ID, TEST_TODO.getName())))).thenReturn(TEST_TODO);

		when(taskRepository.existsById(TEST_TASK_ID)).thenReturn(true);
		when(taskRepository.findById(TEST_TASK_ID)
			.orElseThrow(() -> new NotExistException(TASK_NOT_EXIST_MESSAGE(TEST_TASK_ID, TEST_TASK.getName())))).thenReturn(TEST_TASK);

		when(pivotService.getBoardsReadiness()).thenReturn(testResponse);

		pivotService.getBoardsReadiness();
		*/
	}

}
