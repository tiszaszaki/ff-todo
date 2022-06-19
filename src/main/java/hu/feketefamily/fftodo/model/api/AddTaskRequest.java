package hu.feketefamily.fftodo.model.api;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@Builder
public class AddTaskRequest {
	@NotBlank
	@Size(max = TodoCommon.maxTaskNameLength)
	private String name;
	private Boolean done;
	private Date deadline;
}
