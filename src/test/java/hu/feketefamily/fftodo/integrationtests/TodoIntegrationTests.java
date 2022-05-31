package hu.feketefamily.fftodo.integrationtests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.BoardService;

@Log4j2
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoIntegrationTests {

	private static String VALID_TODO_CREATE_PATH;
	private static String INVALID_TODO_CREATE_PATH;

	private static Long VALID_BOARD_ID;
	private static Long EMPTY_BOARD_ID;

	private static final String VALID_NAME = "validName";
	private static final String VALID_DESCRIPTION = "validDescription";
	private static final int VALID_PHASE = 0;
	private static final Long NON_EXISTENT_ID = 666L;

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
		VALID_BOARD_ID = boardService.addBoard(
			Board.builder()
				.name("todo testing board")
				.description("board description")
				.author("board author")
				.build()
		).getId();

		EMPTY_BOARD_ID = boardService.addBoard(
			Board.builder()
				.name("todo testing board 2")
				.description("board description")
				.author("board author")
				.build()
		).getId();

		VALID_TODO_CREATE_PATH = TodoCommon.boardTodoPath(VALID_BOARD_ID);
		INVALID_TODO_CREATE_PATH = TodoCommon.boardTodoPath(666L);
	}

	@Test
	void addValidTodo() throws Exception {
		mockMvc.perform(
			put(VALID_TODO_CREATE_PATH)
			.content(mapper.writeValueAsString(
				Todo.builder()
					.name(VALID_NAME)
					.description(VALID_DESCRIPTION)
					.phase(VALID_PHASE)
					.build()
			))
			.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidTodos")
	void addInvalidTodo(Todo invalidTodo) throws Exception {
		mockMvc.perform(
			put(VALID_TODO_CREATE_PATH)
				.content(mapper.writeValueAsString(invalidTodo))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE));
	}

	@Test
	void addTodoToInvalidBoard() throws Exception {
		mockMvc.perform(
			put(INVALID_TODO_CREATE_PATH)
				.content(mapper.writeValueAsString(
					Todo.builder()
						.name(VALID_NAME)
						.description(VALID_DESCRIPTION)
						.phase(VALID_PHASE)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.BOARD_NOT_EXIST_MESSAGE));
	}

	@Test
	void getExistingTodo() throws Exception {
		Long validTodoId = todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "2")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getId();
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + validTodoId)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getExistingTodoWithTasks() throws Exception {
		Long validTodoId = todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "2")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getId();
		taskService.addTask(
			validTodoId,
			Task.builder()
				.name("task-" + VALID_NAME + "-2")
				.done(true)
				.build()
		);
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + validTodoId)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getExistingTodoByName() throws Exception {
		String validTodoName = todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "2B")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getName();
		mockMvc.perform(
			get(TodoCommon.todoPath + "/name/" + validTodoName)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getTodosFromBoard() throws Exception {
		mockMvc.perform(
			get(TodoCommon.boardTodoPath(VALID_BOARD_ID) + "s")
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getTodos() throws Exception {
		mockMvc.perform(
			get(TodoCommon.todoPath)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void cloneExistingTodo() throws Exception {
		Long validTodoId = todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "3")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getId();
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + validTodoId + "/clone/" + VALID_PHASE + "/" + VALID_BOARD_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void cloningExistingTodoShouldResultSimilar() throws Exception {
		String thisMethodName = new Object(){}.getClass().getEnclosingMethod().getName();
		String originalTodoName = VALID_NAME + "3B";
		String clonedTodoName = originalTodoName + TodoCommon.todoCloneSuffix;

		Todo originalTodo =	Todo.builder()
			.name(originalTodoName)
			.description(VALID_DESCRIPTION)
			.phase(VALID_PHASE)
			.build();
		Long validTodoId = todoService.addTodo(VALID_BOARD_ID, originalTodo).getId();
		Todo clonedTodo;
		Long clonedTodoId;

		clonedTodo = todoService.cloneTodo(validTodoId, originalTodo.getPhase(), originalTodo.getBoard().getId());
		clonedTodoId = clonedTodo.getId();

		log.info("{}(): original is {{}}", thisMethodName, originalTodo);
		log.info("{}(): cloned is   {{}}", thisMethodName, clonedTodo);

		Assertions.assertEquals(clonedTodoName, clonedTodo.getName());
		Assertions.assertEquals(originalTodo.getDescription(), clonedTodo.getDescription());
		Assertions.assertEquals(originalTodo.getPhase(), clonedTodo.getPhase());

		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + clonedTodoId)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void cloneExistingTodoWithTasks() throws Exception {
		Long validTodoId = todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "4")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getId();
		taskService.addTask(
			validTodoId,
			Task.builder()
				.name("task-" + VALID_NAME)
				.done(false)
				.build()
		);
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + validTodoId + "/clone/" + VALID_PHASE + "/" + VALID_BOARD_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void cloneNonExistentTodo() throws Exception {
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + NON_EXISTENT_ID + "/clone/" + VALID_PHASE + "/" + VALID_BOARD_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeNonExistentTodo() throws Exception {
		mockMvc.perform(
			delete(TodoCommon.todoPath + "/" + NON_EXISTENT_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateNonExistentTodo() throws Exception {
		mockMvc.perform(
			patch(TodoCommon.todoPath + "/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(
					Todo.builder()
						.name(VALID_NAME)
						.description(VALID_DESCRIPTION)
						.phase(VALID_PHASE)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidTodos")
	void updateInvalidTodo(Todo invalidTodo) throws Exception {
		mockMvc.perform(
			patch(TodoCommon.todoPath + "/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(invalidTodo))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void clearNonEmptyTodoList() throws Exception {
		List<Todo> todos = todoService.getTodosFromBoard(VALID_BOARD_ID, false);
		Assertions.assertNotEquals(0, todos.size());
		todoService.addTodo(
			VALID_BOARD_ID,
			Todo.builder()
				.name(VALID_NAME + "5")
				.description(VALID_DESCRIPTION)
				.phase(VALID_PHASE)
				.build()
		).getId();
		mockMvc.perform(
			delete(TodoCommon.boardTodoPath(VALID_BOARD_ID) + "/clear")
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void clearEmptyTodoList() throws Exception {
		List<Todo> todos = todoService.getTodosFromBoard(EMPTY_BOARD_ID, false);
		Assertions.assertEquals(0, todos.size());
		mockMvc.perform(
			delete(TodoCommon.boardTodoPath(EMPTY_BOARD_ID) + "/clear")
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getNameMaxLength() throws Exception {
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + "name-max-length")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(Long.toString(TodoCommon.maxTodoNameLength)));
	}

	@Test
	void getDescriptionMaxLength() throws Exception {
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + "description-max-length")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(Long.toString(TodoCommon.maxTodoDescriptionLength)));
	}

	@Test
	void getTodoPhaseRange() throws Exception {
		String tempBodyStr = new StringBuilder()
			.append("[").append(Long.toString(TodoCommon.phaseMin))
			.append(",").append(Long.toString(TodoCommon.phaseMax))
			.append("]").toString();
		mockMvc.perform(
			get(TodoCommon.todoPath + "/" + "phase-val-range")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(tempBodyStr));
	}

	private static Stream<Arguments> provideInvalidTodos() {
		return Stream.of(
			Arguments.of(Todo.builder() // missing name
				.description(VALID_DESCRIPTION)
				.build()),
			Arguments.of(Todo.builder() // blank name
				.name("")
				.description(VALID_DESCRIPTION)
				.build()),
			Arguments.of(Todo.builder() // description with invalid length
				.name("a".repeat(TodoCommon.maxTodoNameLength + 1))
				.description(VALID_DESCRIPTION)
				.build()),
			Arguments.of(Todo.builder() // description with invalid length
				.name(VALID_NAME)
				.description("a".repeat(TodoCommon.maxTodoDescriptionLength + 1))
				.build())
		);
	}
}
