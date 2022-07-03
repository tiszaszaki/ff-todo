package hu.feketefamily.fftodo.pivot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadinessPivotRepository extends JpaRepository<ReadinessRecord, Long> {
	@Query(
		"SELECT b.id AS id, b.name AS name" +
		//", COUNT(CASE WHEN ta.done THEN 1 ELSE 0 END) AS doneTaskCount" +
		//", COUNT(CASE WHEN ta.name IS NOT NULL THEN 1 ELSE 0 END) AS taskCount" +
		"\nFROM Board b\n" +
		"LEFT JOIN b.todos to\n" +
		"LEFT JOIN to.tasks ta\n" +
		"GROUP BY b.id, b.name\n"
	)
	Set<Long> getAllBoardsReadiness();

	@Query(
		"SELECT to.id AS id, to.name AS name" +
		//", COUNT(CASE WHEN ta.done THEN 1 ELSE 0 END) AS doneTaskCount" +
		//", COUNT(CASE WHEN ta.name IS NOT NULL THEN 1 ELSE 0 END) AS taskCount" +
		"\nFROM Todo to\n" +
		"LEFT JOIN to.tasks ta\n" +
		"GROUP BY to.id, to.name\n"
	)
	Set<Long> getAllTodosReadiness();
}
