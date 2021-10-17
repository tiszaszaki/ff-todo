package hu.feketefamily.fftodo;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.service.BoardService;
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

	@Test
	void addValidBoard() throws Exception {
		mockMvc.perform(
			put(TodoCommon.boardPath)
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
		Long validBoardId = boardService.addBoard(
			Board.builder()
				.name(VALID_NAME + "2")
				.description(VALID_DESCRIPTION)
				.author(VALID_AUTHOR)
				.build()
		).getId();
		mockMvc.perform(
			get(TodoCommon.boardPath + "/" + validBoardId)
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

	@ParameterizedTest
	@MethodSource("provideInvalidBoards")
	void updateInvalidTodo(Board invalidBoard) throws Exception {
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
		).andExpect(content().string(Long.toString(TodoCommon.maxTodoDescriptionLength)));
	}

	private static Stream<Arguments> provideInvalidBoards() {
		return Stream.of(
			Arguments.of(Board.builder().description(VALID_DESCRIPTION).build()),
			Arguments.of(Board.builder().name("").description(VALID_DESCRIPTION).build()),
			Arguments.of(Board.builder().name(VALID_NAME).description("a".repeat(TodoCommon.maxBoardDescriptionLength + 1)).build())
		);
	}
}
