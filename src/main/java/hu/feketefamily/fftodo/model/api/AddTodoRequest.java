package hu.feketefamily.fftodo.model.api;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@Builder
public class AddTodoRequest {
	@NotBlank
	@Size(max = TodoCommon.maxTodoNameLength)
	private String name;
	@Size(max = TodoCommon.maxTodoDescriptionLength)
	private String description;
	@NotNull
	@Min(TodoCommon.todoPhaseMin)
	@Max(TodoCommon.todoPhaseMax)
	private Integer phase;
	private Date deadline;
}
