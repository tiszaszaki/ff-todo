package hu.feketefamily.fftodo.model.repository;

import hu.feketefamily.fftodo.model.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	@Query("select b.id from Board b")
	Set<Long> getAllIds();

	@Modifying
	@Query("UPDATE Board b SET b.name = :name, b.description = :description, b.author = :author WHERE b.id = :id")
	int updateById(@Param("id") Long id, @Param("name") String name, @Param("description") String description, @Param("author") String author);
}
