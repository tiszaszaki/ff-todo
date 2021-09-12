package hu.feketefamily.fftodo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.feketefamily.fftodo.constants.TodoCommon;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false)
	private Long id;
	@NotBlank
	@Column(nullable = false, unique = true)
	private String name;
	@Size(max = TodoCommon.maxBoardDescriptionLength)
	@Column(length = TodoCommon.maxBoardDescriptionLength)
	private String description;
	@Column(nullable = false)
	private String author;
	@PastOrPresent
	@Column(updatable = false, nullable = false)
	private Date dateCreated;
	@Column(nullable = false)
	private Boolean readonlyTodos;
	@Column(nullable = false)
	private Boolean readonlyTasks;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "board")
	@JsonIgnore
	@ToString.Exclude
	private List<Todo> todos;
}
