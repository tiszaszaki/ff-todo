package hu.feketefamily.fftodo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	@Modifying
	@Query("UPDATE Task t SET t.name = :name, t.done = :done WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("done") Boolean done);
	int deleteByTodoId(Long todoId);
}
