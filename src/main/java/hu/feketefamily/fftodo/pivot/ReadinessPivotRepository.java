package hu.feketefamily.fftodo.pivot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadinessPivotRepository extends JpaRepository<ReadinessRecord, Long> {
	@Query(
		"select b.id, b.name, COUNT() as doneTaskCount, COUNT() as taskCount" +
		"from Board b" +
		"left join Todo to on to.boardId = b.id" +
		"left join Task ta on ta.todoId = to.id" +
		"group by b.id" +
		"where ta.done = true"
	)
	Set<ReadinessRecord> getAllBoardsReadiness();

	@Query(
		"select b.id, b.name, COUNT() as doneTaskCount, COUNT() as taskCount" +
		"from Todo to" +
		"left join Task ta on ta.todoId = to.id" +
		"group by to.id" +
		"where ta.done = true"
	)
	Set<ReadinessRecord> getAllTodosReadiness();
}
