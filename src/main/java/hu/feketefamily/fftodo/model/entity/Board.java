package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.pivot.LatestUpdateRecord;
import hu.feketefamily.fftodo.pivot.PivotEntityEvent;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "board")
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxBoardNameLength)
	@Column(nullable = false, unique = true, length = TodoCommon.maxBoardNameLength)
	private String name;
	@Size(max = TodoCommon.maxBoardDescriptionLength)
	@Column(length = TodoCommon.maxBoardDescriptionLength)
	private String description;
	@Size(max = TodoCommon.maxBoardAuthorLength)
	@Column(nullable = false, length = TodoCommon.maxBoardAuthorLength)
	private String author;
	@PastOrPresent
	@Column(updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	@PastOrPresent
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModified;
	@Column(nullable = false)
	private Boolean readonlyTodos;
	@Column(nullable = false)
	private Boolean readonlyTasks;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "board")
	@JsonIgnore
	@ToString.Exclude
	private List<Todo> todos;

	@JsonIgnore
	public Long getDoneTaskCount() {
		return todos.stream().map(Todo::getDoneTaskCount).reduce(0L, Long::sum);
	}
	@JsonIgnore
	public Long getTaskCount() {
		return todos.stream().map(Todo::getTaskCount).reduce(0L, Long::sum);
	}

	@JsonIgnore
	public List<PivotEntityEvent> getEvents()
	{
		var result = new ArrayList<PivotEntityEvent>();
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.ADD_BOARD)
			.time(dateCreated)
			.affectedId(id)
			.affectedName(name)
			.build());
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.UPDATE_BOARD)
			.time(dateModified)
			.affectedId(id)
			.affectedName(name)
			.build());
		if (todos != null) {
			for (var t : todos) {
				result.add(PivotEntityEvent.builder()
					.type(t.getLatestEvent())
					.time(t.getLatestUpdated())
					.affectedId(t.getAffectedId())
					.affectedName(t.getAffectedName())
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
