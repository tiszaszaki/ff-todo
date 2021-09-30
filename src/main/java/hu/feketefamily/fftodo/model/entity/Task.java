package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
	@Column(nullable = false)
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
