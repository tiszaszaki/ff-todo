package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class ReadinessRecord {
	public static Double GetPercent(Long num, Long denom)
	{
		Double result = -1.0;
		if (denom != 0.0)
			result = Double.valueOf(num) / denom;
		return result;
	}

	@Id
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 1, role = "Key", display = "ID")
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	@Column(updatable = false, nullable = false, unique = true)
	@PivotResponse.PivotFetch(order = 2, role = "Key", display = "Name")
	private String name;
	@Column(name = "done_task_count", updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 3, display = "Count of tasks done")
	private Long doneTaskCount;
	@Column(name = "task_count", updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 4, display = "Count of all tasks")
	private Long taskCount;
	@Column(name = "done_task_percent", updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 5, role = "Percent", display = "% of all tasks")
	private Double doneTaskPercent;
}
