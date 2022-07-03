package hu.feketefamily.fftodo.pivot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadinessPivotRepository extends JpaRepository<ReadinessRecord, Long> {
	@Query(
		value =
		"SELECT b.id, b.name" +
		", COUNT(CASE WHEN ta.done THEN 1 ELSE 0 END) AS done_task_count" +
		", COUNT(CASE WHEN ta.name IS NOT NULL THEN 1 ELSE 0 END) AS task_count" +
		", 0.0 AS done_task_percent\n" +
		"FROM Board AS b\n" +
		"LEFT JOIN Todo AS t ON b.id = t.board_id\n" +
		"LEFT JOIN Task AS ta ON t.id = ta.todo_id\n" +
		"GROUP BY b.id, b.name\n"
		, nativeQuery = true
	)
	Set<ReadinessRecord> getAllBoardsReadiness();

	@Query(
		value =
		"SELECT t.id, t.name" +
		", COUNT(CASE WHEN ta.done THEN 1 ELSE 0 END) AS done_task_count" +
		", COUNT(CASE WHEN ta.name IS NOT NULL THEN 1 ELSE 0 END) AS task_count" +
		", 0.0 AS done_task_percent\n" +
		"FROM Todo AS t\n" +
		"LEFT JOIN Task AS ta ON t.id = ta.todo_id\n" +
		"GROUP BY t.id, t.name\n"
		, nativeQuery = true
	)
	Set<ReadinessRecord> getAllTodosReadiness();
}
