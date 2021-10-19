package hu.feketefamily.fftodo.integrationtests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.BoardService;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskIntegrationTests {

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
			Todo.builder()
				.name("todo")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
		VALID_ID = taskService.addTask(
			VALID_TODO_ID,
			Task.builder()
				.name(VALID_NAME)
				.done(VALID_DONE)
				.build()
		).getId();

		NON_EMPTY_TASKLIST_TODO_ID = todoService.addTodo(
			validBoardId,
			Todo.builder()
				.name("todo with only one Task")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
		taskService.addTask(
			NON_EMPTY_TASKLIST_TODO_ID,
			Task.builder()
				.name(VALID_NAME)
				.done(VALID_DONE)
				.build()
		).getId();

		EMPTY_TASKLIST_TODO_ID = todoService.addTodo(
			validBoardId,
			Todo.builder()
				.name("todo with empty Task list")
				.description("todo description")
				.phase(1)
				.build()
		).getId();
	}

	@Test
	void addValidTask() throws Exception {
		Long initTaskCount = 1L;
		List<Task> tasks = todoService.getTodo(VALID_TODO_ID).getTasks();
		Assertions.assertEquals(initTaskCount, tasks.size());

		mockMvc.perform(
			put(TodoCommon.todoTaskPath(VALID_TODO_ID))
			.content(mapper.writeValueAsString(
				Task.builder()
					.name(VALID_NAME)
					.done(VALID_DONE)
					.build()
			))
			.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));

		tasks = todoService.getTodo(VALID_TODO_ID).getTasks();
		Assertions.assertEquals(initTaskCount + 1, tasks.size());
		Task task = tasks.get(1);
		Assertions.assertEquals(VALID_NAME, task.getName());
		Assertions.assertEquals(VALID_DONE, task.getDone());
	}

	@Test
	void addInvalidTask() throws Exception {
		mockMvc.perform(
			put(TodoCommon.todoTaskPath(VALID_TODO_ID))
				.content(mapper.writeValueAsString(Task.builder().done(VALID_DONE).build()))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE));
	}

	@Test
	void addTaskToInvalidTodo() throws Exception {
		mockMvc.perform(
			put(TodoCommon.todoTaskPath(INVALID_TODO_ID))
				.content(mapper.writeValueAsString(
					Task.builder()
						.name(VALID_NAME)
						.done(VALID_DONE)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.TODO_NOT_EXIST_MESSAGE));
	}

	@Test
	void updateNonExistentTask() throws Exception {
		mockMvc.perform(
			patch(TodoCommon.taskPath + "/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(
					Task.builder()
						.name(VALID_NAME)
						.done(VALID_DONE)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateValidTasks() throws Exception {
		mockMvc.perform(
			patch(TodoCommon.taskPath + "/" + VALID_ID)
				.content(mapper.writeValueAsString(
					Task.builder()
						.name(VALID_NAME)
						.done(VALID_DONE)
						.build()))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateInvalidTasks() throws Exception {
		mockMvc.perform(
			patch(TodoCommon.taskPath + "/" + VALID_ID)
				.content(mapper.writeValueAsString(
					Task.builder()
						.done(VALID_DONE)
						.build()))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void removeExistingTask() throws Exception {
		mockMvc.perform(
			delete(TodoCommon.taskPath + "/" + VALID_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeNonExistentTask() throws Exception {
		mockMvc.perform(
			delete(TodoCommon.taskPath + "/" + NON_EXISTENT_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void clearNonEmptyTaskList() throws Exception {
		List<Task> tasks = todoService.getTodo(NON_EMPTY_TASKLIST_TODO_ID).getTasks();
		Assertions.assertNotEquals(0, tasks.size());
		mockMvc.perform(
			delete(TodoCommon.todoTaskPath(NON_EMPTY_TASKLIST_TODO_ID) + "/clear")
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void clearEmptyTaskList() throws Exception {
		List<Task> tasks = todoService.getTodo(EMPTY_TASKLIST_TODO_ID).getTasks();
		Assertions.assertEquals(0, tasks.size());
		mockMvc.perform(
			delete(TodoCommon.todoTaskPath(EMPTY_TASKLIST_TODO_ID) + "/clear")
		).andExpect(status().is(HttpStatus.OK.value()));
	}
}