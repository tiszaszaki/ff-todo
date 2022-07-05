package hu.feketefamily.fftodo.model.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.pivot.LatestUpdateRecord;
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
	public Date getLatestTodoUpdated()
	{
		var compareVal = dateCreated.compareTo(dateModified);
		if (compareVal < 0)
			return dateModified;
		else
			return dateCreated;
	}
	@JsonIgnore
	public LatestUpdateRecord.LatestUpdateEvent getLatestTodoEvent()
	{
		if (getLatestTodoUpdated().compareTo(dateModified) == 0)
			return LatestUpdateRecord.LatestUpdateEvent.UPDATE_TODO;
		else
			return LatestUpdateRecord.LatestUpdateEvent.ADD_TODO;
	}

	@JsonIgnore
	public Date getLatestUpdated()
	{
		var result = getLatestTodoUpdated();
		if (tasks.size() > 0) {
			var temp = tasks.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var compareVal = result.compareTo(temp);
			if (compareVal < 0)
				result = temp;
		}
		return result;
	}
	@JsonIgnore
	public LatestUpdateRecord.LatestUpdateEvent getLatestEvent()
	{
		var result = getLatestTodoEvent();
		if (tasks.size() > 0) {
			var temp1 = getLatestTodoUpdated();
			var temp2 = tasks.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = tasks.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getLatestEvent();
			var compareVal = temp1.compareTo(temp2);
			if (compareVal < 0)
				result = temp3;
		}
		return result;
	}

	@JsonIgnore
	public Long getAffectedId()
	{
		var result = id;
		if (tasks.size() > 0) {
			var temp1 = getLatestTodoUpdated();
			var temp2 = tasks.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = tasks.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getId();
			var compareVal = temp1.compareTo(temp2);
			if (compareVal < 0)
				result = temp3;
		}
		return result;
	}
	@JsonIgnore
	public String getAffectedName()
	{
		var result = name;
		if (tasks.size() > 0) {
			var temp1 = getLatestTodoUpdated();
			var temp2 = tasks.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = tasks.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getName();
			var compareVal = temp1.compareTo(temp2);
			if (compareVal < 0)
				result = temp3;
		}
		return result;
	}
}
