package hu.feketefamily.fftodo.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.pivot.LatestUpdateRecord;
import hu.feketefamily.fftodo.pivot.PivotEntityEvent;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todo")
public class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxTodoNameLength)
	@Column(nullable = false, unique = true, length = TodoCommon.maxTodoNameLength)
	private String name;
	@Size(max = TodoCommon.maxTodoDescriptionLength)
	@Column(length = TodoCommon.maxTodoDescriptionLength)
	private String description;
	@NotNull
	@Min(TodoCommon.todoPhaseMin)
	@Max(TodoCommon.todoPhaseMax)
	private Integer phase;
	@PastOrPresent
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	@PastOrPresent
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModified;
	@Temporal(TemporalType.TIMESTAMP)
	private Date deadline;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "todo")
	private List<Task> tasks;
	@ManyToOne
	@JoinColumn(name = "board_id", nullable = false)
	@JsonIgnore
	@ToString.Exclude
	private Board board;

	@JsonIgnore
	public Long getDoneTaskCount() {
		return tasks.stream().filter(Task::getDone).count();
	}
	@JsonIgnore
	public Long getTaskCount() {
		return Long.valueOf(tasks.size());
	}

	@JsonIgnore
	public List<PivotEntityEvent> getEvents()
	{
		var result = new ArrayList<PivotEntityEvent>();
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.ADD_TODO)
			.time(dateCreated)
			.affectedId(id)
			.affectedName(name)
			.build());
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.UPDATE_TODO)
			.time(dateModified)
			.affectedId(id)
			.affectedName(name)
			.build());
		if (tasks != null) {
			for (var t : tasks) {
				result.add(PivotEntityEvent.builder()
					.type(t.getLatestEvent())
					.time(t.getLatestUpdated())
					.affectedId(t.getId())
					.affectedName(t.getName())
					.build());
			}
		}
		return result;
	}

	@JsonIgnore
	public Date getLatestUpdated()
	{
		return getEvents().stream().map(e -> e.getTime()).max(Date::compareTo).orElseThrow();
	}
	@JsonIgnore
	public LatestUpdateRecord.LatestUpdateEvent getLatestEvent()
	{
		return getEvents().stream().filter(e -> e.getTime() == getLatestUpdated()).findFirst().orElseThrow().getType();
	}

	@JsonIgnore
	public Long getAffectedId()
	{
		return getEvents().stream().filter(e -> e.getTime() == getLatestUpdated()).findFirst().orElseThrow().getAffectedId();
	}
	@JsonIgnore
	public String getAffectedName()
	{
		return getEvents().stream().filter(e -> e.getTime() == getLatestUpdated()).findFirst().orElseThrow().getAffectedName();
	}
}
