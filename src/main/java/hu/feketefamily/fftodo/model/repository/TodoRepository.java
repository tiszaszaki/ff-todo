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
		"SELECT CONCAT(t2.name,'" + TodoCommon.todoCloneSuffix + "'), t2.description, :phase, " +
		":date_new, :date_new, t2.board FROM Todo AS t2 WHERE t2.id = :id\n")
	int cloneById(@Param("id") Long id, @Param("phase") Integer phase, @Param("date_new") Date dateNew);

	@Modifying
	@Query("UPDATE Todo t SET t.name = :name, t.description = :description, t.phase = :phase, t.dateModified= :now WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("description") String description, @Param("phase") int phase, @Param("now") Date dateModified);
	int deleteByBoardId(Long boardId);
	List<Todo> findByBoardId(Long boardId);
}
