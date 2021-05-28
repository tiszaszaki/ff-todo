package hu.feketefamily.fftodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.model.repository.TodoRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TodoService {

	@Autowired
	private TodoRepository todoRepository;

	public List<Todo> getTodos() {
		return todoRepository.findAll();
	}

	public Todo addTodo(Todo todo) {
		return todoRepository.save(todo);
	}

	public void removeTodo(Long id) {
		if (todoRepository.existsById(id)) {
			todoRepository.deleteById(id);
		} else {
			log.warn("Deleting non-existing Todo with id {{}}", id);
		}
	}
}
