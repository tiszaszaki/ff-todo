package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ReadinessRecord {
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	private String name;
	private Long doneTaskCount;
	private Long taskCount;
}
