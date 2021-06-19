package hu.feketefamily.fftodo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.model.entity.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoIntegrationTests {

	private static Long VALID_ID = 1L;
	private static String VALID_NAME = "validName";
	private static String VALID_DESCRIPTION = "validDescription";
	private static int VALID_PHASE = 0;

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
			delete("/todo/" + VALID_ID)
		).andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	void updateNonExistentTodo() throws Exception {
		mockMvc.perform(
			patch("/todo/" + VALID_ID)
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
			patch("/todo/" + VALID_ID)
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
