package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

	@Id
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	@Column(updatable = false, nullable = false, unique = true)
	private String name;
	@Column(name = "done_task_count", updatable = false, nullable = false)
	private Long doneTaskCount;
	@Column(name = "task_count", updatable = false, nullable = false)
	private Long taskCount;
	@Column(name = "done_task_percent", updatable = false, nullable = false)
	private Double doneTaskPercent;
}
