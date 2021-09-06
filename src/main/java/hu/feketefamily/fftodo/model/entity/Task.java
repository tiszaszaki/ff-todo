package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
	private Date deadline;
	@ManyToOne
	@JoinColumn(name = "todo_id", nullable = false)
	@JsonIgnore
	@ToString.Exclude
	private Todo todo;
}
