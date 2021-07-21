package hu.feketefamily.fftodo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import hu.feketefamily.fftodo.model.entity.Todo;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoIntegrationTests {

	private static final String VALID_NAME = "validName";
	private static final String VALID_DESCRIPTION = "validDescription";
	private static final int VALID_PHASE = 0;
	private static final Long NON_EXISTENT_ID = 1L;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void addValidTodo() throws Exception {
		mockMvc.perform(
			put("/todo")
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
			put("/todo")
				.content(mapper.writeValueAsString(invalidTodo))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(content().string(ErrorMessages.CONSTRAINT_VIOLATION_MESSAGE));
	}

	@Test
	void getTodos() throws Exception {
		mockMvc.perform(
			get("/todo")
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void removeNonExistentTodo() throws Exception {
		mockMvc.perform(
			delete("/todo/" + NON_EXISTENT_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateNonExistentTodo() throws Exception {
		mockMvc.perform(
			patch("/todo/" + NON_EXISTENT_ID)
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
			patch("/todo/" + NON_EXISTENT_ID)
				.content(mapper.writeValueAsString(invalidTodo))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}

	private static Stream<Arguments> provideInvalidTodos() {
		return Stream.of(
			Arguments.of(Todo.builder().description(VALID_DESCRIPTION).build()),
			Arguments.of(Todo.builder().name("").description(VALID_DESCRIPTION).build()),
			Arguments.of(Todo.builder().name(VALID_NAME).description("a".repeat(1025)).build())
		);
	}
}
