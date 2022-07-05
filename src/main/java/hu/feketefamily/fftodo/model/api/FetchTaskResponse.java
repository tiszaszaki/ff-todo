package hu.feketefamily.fftodo.model.api;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
public class FetchTaskResponse {
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxTaskNameLength)
	private String name;
	private Boolean done;
	@PastOrPresent
	private Date dateCreated;
	@PastOrPresent
	private Date dateModified;
	private Date deadline;
	private Long todoId;
}
