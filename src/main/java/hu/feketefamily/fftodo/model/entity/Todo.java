package hu.feketefamily.fftodo.model.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.*;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todo")
public class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Column(nullable = false, unique = true)
	private String name;
	@Size(max = TodoCommon.maxDescriptionLength)
	@Column(length = TodoCommon.maxDescriptionLength)
	private String description;
	@Formula("LENGTH(description)")
	private Long descriptionLength;
	@NotNull
	@Min(TodoCommon.phaseMin)
	@Max(TodoCommon.phaseMax)
	private Integer phase;
	@PastOrPresent
	@Column(nullable = false)
	private Date dateCreated;
	@PastOrPresent
	@Column(nullable = false)
	private Date dateModified;
	private Date deadline;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "todo")
	private List<Task> tasks;
	@Formula("(SELECT COUNT(t.todo_id) FROM Task t WHERE t.todo_id = id)")
	private Long taskCount;

	/*
	@AssertTrue
	public Boolean isValid() {
		Boolean result = true;
		result &= (!this.dateModified.before(this.dateCreated));
		result &= (!this.deadline.before(this.dateCreated));
		result &= (!this.deadline.before(this.dateModified));
		return result;
	}
	*/
}
