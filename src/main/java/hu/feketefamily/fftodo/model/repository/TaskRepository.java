package hu.feketefamily.fftodo.model.repository;

import hu.feketefamily.fftodo.constants.TodoCommon;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Task;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	@Modifying
	@Query(
		"INSERT INTO Task (name, done, todo, dateCreated, dateModified)\n" +
		"SELECT name, done, (SELECT t2 FROM Todo AS t2 WHERE t2.name = :name_new), " +
		":date_new, :date_new FROM Task AS t WHERE t.todo.id = :id")
	int cloneByTodoId(@Param("id") Long todoId, @Param("name_new") String todoNameNew, @Param("date_new") Date dateNew);

	@Modifying
	@Query("UPDATE Task t SET t.name = :name, t.done = :done," +
		"t.dateModified = :now, t.deadline = :deadline WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("done") Boolean done,
				   @Param("now") Date dateModified, @Param("deadline") Date deadline);
	int deleteByTodoId(Long todoId);
	List<Task> findByTodoId(Long todoId);
	List<Task> findByTodoId(Long todoId, Sort sort);
}
