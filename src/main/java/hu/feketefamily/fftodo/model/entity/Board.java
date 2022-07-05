package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.pivot.LatestUpdateRecord;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
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
	public Date getLatestBoardUpdated()
	{
		var compareVal = dateCreated.compareTo(dateModified);
		if (compareVal < 0)
			return dateModified;
		else
			return dateCreated;
	}
	@JsonIgnore
	public LatestUpdateRecord.LatestUpdateEvent getLatestBoardEvent()
	{
		if (getLatestBoardUpdated().compareTo(dateModified) == 0)
			return LatestUpdateRecord.LatestUpdateEvent.UPDATE_BOARD;
		else
			return LatestUpdateRecord.LatestUpdateEvent.ADD_BOARD;
	}

	@JsonIgnore
	public Date getLatestUpdated()
	{
		var result = getLatestBoardUpdated();
		if (todos.size() > 0) {
			var temp = todos.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var compareVal = result.compareTo(temp);
			if (compareVal < 0)
				result = temp;
		}
		return result;
	}
	@JsonIgnore
	public LatestUpdateRecord.LatestUpdateEvent getLatestEvent()
	{
		var result = getLatestBoardEvent();
		if (todos.size() > 0) {
			var temp1 = getLatestBoardUpdated();
			var temp2 = todos.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = todos.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getLatestEvent();
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
		if (todos.size() > 0) {
			var temp1 = getLatestBoardUpdated();
			var temp2 = todos.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = todos.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getId();
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
		if (todos.size() > 0) {
			var temp1 = getLatestBoardUpdated();
			var temp2 = todos.stream().map(t -> t.getLatestUpdated()).max(Date::compareTo).get();
			var temp3 = todos.stream().filter(t -> t.getLatestUpdated() == temp2).findFirst().orElseThrow().getName();
			var compareVal = temp1.compareTo(temp2);
			if (compareVal < 0)
				result = temp3;
		}
		return result;
	}
}
