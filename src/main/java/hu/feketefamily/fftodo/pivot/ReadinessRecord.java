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
	public static List<String> fieldOrder() {
		var res = new LinkedList<String>();
		res.add("id"); res.add("name");
		res.add("doneTaskCount"); res.add("taskCount");
		res.add("doneTaskPercent");
		return res;
	}
	public static Map<String, String> fieldRoles() {
		var res = new HashMap<String, String>();
		res.put("id", "Key");
		res.put("name", "Key");
		res.put("doneTaskCount", "");
		res.put("taskCount", "");
		res.put("doneTaskPercent", "Percent");
		return res;
	}

	public static Set<PivotResponseFieldPair> fieldDisplay() {
		var res = new HashSet<PivotResponseFieldPair>();
		res.add(new PivotResponseFieldPair("id", "ID"));
		res.add(new PivotResponseFieldPair("name", "Name"));
		res.add(new PivotResponseFieldPair("doneTaskCount", "Count of tasks done"));
		res.add(new PivotResponseFieldPair("taskCount", "Count of all tasks"));
		res.add(new PivotResponseFieldPair("doneTaskPercent", "% of tasks done"));
		return res;
	}

	@Id
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	@Column(updatable = false, nullable = false, unique = true)
	@PivotResponse.PivotFetch
	private String name;
	@Column(name = "done_task_count", updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private Long doneTaskCount;
	@Column(name = "task_count", updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private Long taskCount;
	@Column(name = "done_task_percent", updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private Double doneTaskPercent;
}
