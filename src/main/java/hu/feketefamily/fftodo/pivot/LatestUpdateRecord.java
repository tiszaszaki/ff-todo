package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class LatestUpdateRecord {
	public enum LatestUpdateEvent {
		ADD_BOARD,
		ADD_TODO,
		ADD_TASK,
		UPDATE_BOARD,
		UPDATE_TODO,
		UPDATE_TASK
	}

	public static List<String> fieldOrder() {
		var res = new LinkedList<String>();
		res.add("id"); res.add("name");
		res.add("latestUpdated"); res.add("latestEvent");
		res.add("affectedId"); res.add("affectedName");
		return res;
	}
	public static Map<String, String> fieldRoles() {
		var res = new HashMap<String, String>();
		res.put("id", "Key");
		res.put("name", "Key");
		res.put("latestUpdated", "");
		res.put("latestEvent", "");
		res.put("affectedId", "");
		res.put("affectedName", "");
		return res;
	}

	public static Set<PivotResponseFieldPair> fieldDisplay() {
		var res = new HashSet<PivotResponseFieldPair>();
		res.add(new PivotResponseFieldPair("id", "ID"));
		res.add(new PivotResponseFieldPair("name", "Name"));
		res.add(new PivotResponseFieldPair("latestUpdated", "Date of latest event"));
		res.add(new PivotResponseFieldPair("latestEvent", "Type of latest event"));
		res.add(new PivotResponseFieldPair("affectedId", "ID of entity affected"));
		res.add(new PivotResponseFieldPair("affectedName", "Name of entity affected"));
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
	@PastOrPresent
	@Column(name = "latest_updated", updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@PivotResponse.PivotFetch
	private Date latestUpdated;
	@Column(name = "latest_event", updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private LatestUpdateEvent latestEvent;
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private Long affectedId;
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch
	private String affectedName;
}
