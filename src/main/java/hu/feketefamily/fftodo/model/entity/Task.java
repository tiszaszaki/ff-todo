package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.pivot.LatestUpdateRecord;
import hu.feketefamily.fftodo.pivot.PivotEntityEvent;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxTaskNameLength)
	@Column(nullable = false, length = TodoCommon.maxTaskNameLength)
	private String name;
	@Column(nullable = false)
	private Boolean done;
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
	@ManyToOne
	@JoinColumn(name = "todo_id", nullable = false)
	@JsonIgnore
	@ToString.Exclude
	private Todo todo;

	@JsonIgnore
	public List<PivotEntityEvent> getEvents()
	{
		var result = new ArrayList<PivotEntityEvent>();
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.ADD_TASK)
			.time(dateCreated)
			.affectedId(id)
			.affectedName(name)
			.build());
		result.add(PivotEntityEvent.builder()
			.type(LatestUpdateRecord.LatestUpdateEvent.UPDATE_TASK)
			.time(dateModified)
			.affectedId(id)
			.affectedName(name)
			.build());
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
}
