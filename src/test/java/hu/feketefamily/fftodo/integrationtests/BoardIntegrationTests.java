package hu.feketefamily.fftodo.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.service.BoardService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardIntegrationTests {
	private static Long VALID_BOARD_ID;

	private static final String VALID_NAME = "validName";
	private static final String VALID_DESCRIPTION = "validDescription";
	private static final String VALID_AUTHOR = "validAuthor";
	private static final Long NON_EXISTENT_ID = 666L;

	@Autowired
	private BoardService boardService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@BeforeAll
	void beforeAll() {
		VALID_BOARD_ID = boardService.addBoard(
			Board.builder()
				.name(VALID_NAME)
				.description(VALID_DESCRIPTION)
				.author(VALID_AUTHOR)
				.build()
		).getId();
	}

	@Test
	void addValidBoard() throws Exception {
		mockMvc.perform(
			put(TodoCommon.boardPath)
				.content(mapper.writeValueAsString(
					Board.builder()
						.name(VALID_NAME + "2")
						.description(VALID_DESCRIPTION)
						.author(VALID_AUTHOR)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidBoards")
	void addInvalidBoard(Board invalidBoard) throws Exception {
		mockMvc.perform(
				put(TodoCommon.boardPath)
					.content(mapper.writeValueAsString(invalidBoard))
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE));
	}

	@Test
	void getExistingBoard() throws Exception {
		mockMvc.perform(
			get(TodoCommon.boardPath + "/" + VALID_BOARD_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void getAllBoards() throws Exception {
		mockMvc.perform(
			get(TodoCommon.boardPath)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeNonExistentBoard() throws Exception {
		mockMvc.perform(
			delete(TodoCommon.boardPath + "/" + NON_EXISTENT_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeExistingBoard() throws Exception {
		Long existingBoardId = boardService.addBoard(
			Board.builder()
				.name(VALID_NAME + "3")
				.description(VALID_DESCRIPTION)
				.author(VALID_AUTHOR)
				.build()
		).getId();
		mockMvc.perform(
			delete(TodoCommon.boardPath + "/" + existingBoardId)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateNonExistentBoard() throws Exception {
		mockMvc.perform(
			patch(TodoCommon.boardPath + "/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(
					Board.builder()
						.name(VALID_NAME)
						.description(VALID_DESCRIPTION)
						.author(VALID_AUTHOR)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateValidBoard() throws Exception {
		Long validBoardId = boardService.addBoard(
			Board.builder()
				.name(VALID_NAME + "4")
				.description(VALID_DESCRIPTION)
				.author(VALID_AUTHOR)
				.build()
		).getId();
		mockMvc.perform(
			patch(TodoCommon.boardPath + "/" + validBoardId)
				.content(mapper.writeValueAsString(
					Board.builder()
						.name(VALID_NAME + "5")
						.description(VALID_DESCRIPTION)
						.author(VALID_AUTHOR)
						.build()
				))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidBoards")
	void updateInvalidBoard(Board invalidBoard) throws Exception {
		mockMvc.perform(
			patch(TodoCommon.boardPath + "/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(invalidBoard))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	void getDescriptionMaxLength() throws Exception {
		mockMvc.perform(
			get(TodoCommon.boardPath + "/" + "description-max-length")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(Long.toString(TodoCommon.maxBoardDescriptionLength)));
	}

	@ParameterizedTest
	@MethodSource("provideBooleanValues")
	void accessReadonlyTodoSetting(Boolean readonlyVal) throws Exception {
		mockMvc.perform(
			patch(TodoCommon.boardPath + "/" + VALID_BOARD_ID + "/readonly-todos/" + readonlyVal)
		).andExpect(status().is(HttpStatus.OK.value()));
		mockMvc.perform(
			get(TodoCommon.boardPath + "/" + VALID_BOARD_ID + "/readonly-todos")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(Boolean.toString(readonlyVal)));
	}

	@ParameterizedTest
	@MethodSource("provideBooleanValues")
	void accessReadonlyTaskSetting(Boolean readonlyVal) throws Exception {
		mockMvc.perform(
			patch(TodoCommon.boardPath + "/" + VALID_BOARD_ID + "/readonly-tasks/" + readonlyVal)
		).andExpect(status().is(HttpStatus.OK.value()));
		mockMvc.perform(
			get(TodoCommon.boardPath + "/" + VALID_BOARD_ID + "/readonly-tasks")
		).andExpect(status().is(HttpStatus.OK.value())
		).andExpect(content().string(Boolean.toString(readonlyVal)));
	}

	private static Stream<Arguments> provideBooleanValues() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

	private static Stream<Arguments> provideInvalidBoards() {
		return Stream.of(
			Arguments.of(Board.builder().description(VALID_DESCRIPTION).build()),
			Arguments.of(Board.builder().name("").description(VALID_DESCRIPTION).build()),
			Arguments.of(Board.builder().name(VALID_NAME).description("a".repeat(TodoCommon.maxBoardDescriptionLength + 1)).build())
		);
	}
}
