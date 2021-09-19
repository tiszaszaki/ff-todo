package hu.feketefamily.fftodo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.stream.Stream;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.BoardService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoIntegrationTests {

	private static final String todoPath = TodoCommon.todoPath;
	private static final String todoPath2 = todoPath + "/";

	private static String VALID_TODO_CREATE_PATH;
	private static String INVALID_TODO_CREATE_PATH;

	private static final String VALID_NAME = "validName";
	private static final String VALID_DESCRIPTION = "validDescription";
	private static final int VALID_PHASE = 0;
	private static final Long NON_EXISTENT_ID = 1L;

	@Autowired
	private BoardService boardService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	void beforeAll() {
		var validTodoId = boardService.addBoard(
			Board.builder()
				.name("todo testing board")
				.description("board description")
				.author("board author")
				.build()
		).getId();
		VALID_TODO_CREATE_PATH = TodoCommon.boardPath + "/" + validTodoId + "/todo";
		INVALID_TODO_CREATE_PATH = TodoCommon.boardPath + "/666/todo";
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
	void getTodos() throws Exception {
		mockMvc.perform(
			get(todoPath)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeNonExistentTodo() throws Exception {
		mockMvc.perform(
			delete(todoPath2 + NON_EXISTENT_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateNonExistentTodo() throws Exception {
		mockMvc.perform(
			patch(todoPath2 + NON_EXISTENT_ID)
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
			patch(todoPath2 + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(invalidTodo))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	private static Stream<Arguments> provideInvalidTodos() {
		return Stream.of(
			Arguments.of(Todo.builder().description(VALID_DESCRIPTION).build()),
			Arguments.of(Todo.builder().name("").description(VALID_DESCRIPTION).build()),
			Arguments.of(Todo.builder().name(VALID_NAME).description("a".repeat(TodoCommon.maxTodoDescriptionLength + 1)).build())
		);
	}
}
