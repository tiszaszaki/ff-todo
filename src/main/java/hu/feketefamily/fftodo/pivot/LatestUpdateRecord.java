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

	@Id
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 1, role = "Key", display = "ID")
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	@Column(updatable = false, nullable = false, unique = true)
	@PivotResponse.PivotFetch(order = 2, role = "Key", display = "Name")
	private String name;
	@PastOrPresent
	@Column(name = "latest_updated", updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@PivotResponse.PivotFetch(order = 3, display = "Date of latest event")
	private Date latestUpdated;
	@Column(name = "latest_event", updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 4, display = "Type of latest event")
	private String latestEvent;
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 5, display = "ID of entity affected")
	private Long affectedId;
	@Column(updatable = false, nullable = false)
	@PivotResponse.PivotFetch(order = 6, display = "Name of entity affected")
	private String affectedName;
}
