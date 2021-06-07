package hu.feketefamily.fftodo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	@Modifying
	@Query("UPDATE Todo t SET t.name = :name, t.description = :description WHERE t.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("description") String description);
}
