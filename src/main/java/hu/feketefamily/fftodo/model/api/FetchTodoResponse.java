package hu.feketefamily.fftodo.model.api;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@Builder
public class FetchTodoResponse {
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxTodoNameLength)
	private String name;
	@Size(max = TodoCommon.maxTodoDescriptionLength)
	private String description;
	@NotNull
	@Min(TodoCommon.todoPhaseMin)
	@Max(TodoCommon.todoPhaseMax)
	private Integer phase;
	@PastOrPresent
	private Date dateCreated;
	@PastOrPresent
	private Date dateModified;
	private Date deadline;
	private Long boardId;
}
