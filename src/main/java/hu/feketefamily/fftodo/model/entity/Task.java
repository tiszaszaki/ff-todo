package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import hu.feketefamily.fftodo.constants.TodoCommon;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Size(max = TodoCommon.maxTaskNameLength)
	@Column(nullable = false, length = TodoCommon.maxTaskNameLength)
	private String name;
	@Column(nullable = false)
	private Boolean done;
	@Temporal(TemporalType.TIMESTAMP)
	private Date deadline;
	@ManyToOne
	@JoinColumn(name = "todo_id", nullable = false)
	@JsonIgnore
	@ToString.Exclude
	private Todo todo;
}
