package hu.feketefamily.fftodo.pivot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadinessPivotRepository extends JpaRepository<ReadinessRecord, Long> {
	@Query(
		value =
		"select b.id, b.name" +
		", sum(coalesce(done_todos.dtc, 0)) as done_task_count, sum(coalesce(done_todos.tc, 0)) as task_count, 0.0 as done_task_percent from board as b\n" +
		"left join\n" +
		"(select t.board_id, coalesce(done_tasks.dtc, 0) as dtc, coalesce(done_tasks.tc, 0) as tc from todo t\n" +
		"left join\n" +
		"(select ta.todo_id, count(case when ta.done then 1 end) as dtc, count(*) as tc from task ta group by ta.todo_id) as done_tasks\n" +
		"on t.id = done_tasks.todo_id) as done_todos\n" +
		"on b.id = done_todos.board_id\n" +
		"group by b.id, b.name\n"
		, nativeQuery = true
	)
	Set<ReadinessRecord> getAllBoardsReadiness();

	@Query(
		value =
		"select t.id, t.name" +
		", sum(coalesce(done_tasks.dtc, 0)) as done_task_count, sum(coalesce(done_tasks.tc, 0)) as task_count, 0.0 as done_task_percent from todo as t\n" +
		"left join\n" +
		"(select ta.todo_id, count(case when ta.done then 1 end) as dtc, count(*) as tc from task ta group by ta.todo_id) as done_tasks\n" +
		"on t.id = done_tasks.todo_id\n" +
		"group by t.id, t.name\n"
		, nativeQuery = true
	)
	Set<ReadinessRecord> getAllTodosReadiness();
}
