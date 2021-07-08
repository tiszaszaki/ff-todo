package hu.feketefamily.fftodo.model.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todo")
public class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(nullable = false, unique = true)
	private String name;
	@Size(max = 1024)
	@Column(length = 1024)
	private String description;
	@NotNull
	@Min(0)
	@Max(2)
	private Integer phase;
	@Column(nullable = false)
	private Date dateCreated;
	@Column(nullable = false)
	private Date dateModified;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "todo")
	private Set<Task> tasks;
}
