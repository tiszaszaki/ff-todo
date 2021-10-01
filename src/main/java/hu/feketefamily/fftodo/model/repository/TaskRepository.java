package hu.feketefamily.fftodo.model.repository;

import hu.feketefamily.fftodo.constants.TodoCommon;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	@Modifying
	@Query(
		"INSERT INTO Task (name, done, todo)\n" +
		"SELECT name, done, (SELECT t2 FROM Todo AS t2 WHERE t2.name = CONCAT(:name,'" + TodoCommon.todoCloneSuffix + "'))" +
		"FROM Task AS t WHERE t.todo.id = :id")
	int cloneByTodoId(@Param("id") Long todoId, @Param("name") String todoName);

	@Modifying
	@Query("UPDATE Task t SET t.name = :name, t.done = :done WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("done") Boolean done);
	int deleteByTodoId(Long todoId);
	List<Task> findByTodoId(Long todoId);
	List<Task> findByTodoId(Long todoId, Sort sort);
}
