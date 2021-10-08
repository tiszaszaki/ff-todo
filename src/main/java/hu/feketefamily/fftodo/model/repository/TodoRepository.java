package hu.feketefamily.fftodo.model.repository;

import hu.feketefamily.fftodo.constants.TodoCommon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Todo;

import java.util.Date;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	@Modifying
	@Query(
		"INSERT INTO Todo (name, description, phase, dateCreated, dateModified, board)\n" +
		"SELECT CONCAT(t.name,'" + TodoCommon.todoCloneSuffix + "'), t.description, :phase, " +
		":date_new, :date_new, (SELECT b FROM Board AS b WHERE b.id = :boardId) FROM Todo AS t WHERE t.id = :id\n")
	int cloneById(@Param("id") Long id, @Param("phase") Integer phase, @Param("boardId") Long boardId, @Param("date_new") Date dateNew);

	@Modifying
	@Query("UPDATE Todo t SET t.name = :name, t.description = :description, t.phase = :phase," +
		"t.dateModified = :now, t.deadline = :deadline WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("description") String description, @Param("phase") int phase,
				   @Param("now") Date dateModified, @Param("deadline") Date deadline);
	int deleteByBoardId(Long boardId);
	List<Todo> findByBoardId(Long boardId);
}
