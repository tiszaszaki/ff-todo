package hu.feketefamily.fftodo.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.api.AddTaskRequest;
import hu.feketefamily.fftodo.model.api.AddTodoRequest;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.service.BoardService;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PivotIntegrationTests {

	private static final String VALID_NAME = "validName";
	private static final Boolean VALID_DONE = true;
	private static Long VALID_ID;
	private static final Long NON_EXISTENT_ID = 123L;
	private static Long VALID_TODO_ID;
	private static final Long INVALID_TODO_ID = 666L;

	private static Long NON_EMPTY_TASKLIST_TODO_ID;
	private static Long EMPTY_TASKLIST_TODO_ID;

	@Autowired
	private BoardService boardService;

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	void beforeAll() {
		var validBoardId = boardService.addBoard(
			Board.builder()
				.name("task testing board")
				.description("board description")
				.author("board author")
				.build()
		).getId();

		VALID_TODO_ID = todoService.addTodo(
			validBoardId,
			AddTodoRequest.builder()
				.name("todo")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
		VALID_ID = taskService.addTask(
			VALID_TODO_ID,
			AddTaskRequest.builder()
				.name(VALID_NAME)
				.done(VALID_DONE)
				.build()
		).getId();

		NON_EMPTY_TASKLIST_TODO_ID = todoService.addTodo(
			validBoardId,
			AddTodoRequest.builder()
				.name("todo with only one Task")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
		taskService.addTask(
			NON_EMPTY_TASKLIST_TODO_ID,
			AddTaskRequest.builder()
				.name(VALID_NAME)
				.done(VALID_DONE)
				.build()
		).getId();

		EMPTY_TASKLIST_TODO_ID = todoService.addTodo(
			validBoardId,
			AddTodoRequest.builder()
				.name("todo with empty Task list")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
	}

	@Test
	void getBoardReadiness() throws Exception {
		mockMvc.perform(
			get(TodoCommon.pivotPath + "/" + TodoCommon.pivotLabel1)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getTodoReadiness() throws Exception {
		mockMvc.perform(
			get(TodoCommon.pivotPath + "/" + TodoCommon.pivotLabel2)
		).andExpect(status().is(HttpStatus.OK.value()));
	}
}
