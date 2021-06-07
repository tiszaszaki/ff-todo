package hu.feketefamily.fftodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		log.info("Saving Todo: {{}}", todo.toString());
		return todoRepository.save(todo);
	}

	public void removeTodo(Long id) {
		if (todoRepository.existsById(id)) {
			log.info("Deleting Todo with id: {{}}", id);
			todoRepository.deleteById(id);
		} else {
			log.warn("Deleting non-existing Todo with id {{}}", id);
		}
	}

	@Transactional
	public void updateTodo(Long id, Todo patchedTodo) {
		if (todoRepository.updateById(id, patchedTodo.getName(), patchedTodo.getDescription()) < 1) {
			log.warn("No Todos were updated with id {{}}", id);
		}
	}
}
