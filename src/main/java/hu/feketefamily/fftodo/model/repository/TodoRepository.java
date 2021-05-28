package hu.feketefamily.fftodo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.feketefamily.fftodo.model.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
