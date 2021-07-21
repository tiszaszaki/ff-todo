package hu.feketefamily.fftodo.service;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.model.repository.TodoRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Validated
public class TodoService {

	@Autowired
	private TodoRepository todoRepository;

	public Todo getTodo(Long id) {
		return todoRepository.findById(id).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE) );
	}

	public List<Todo> getTodos() {
		return todoRepository.findAll();
	}

	public Todo addTodo(@Valid Todo todo) {
		Date now=new Date();
		todo.setDateCreated(now);
		todo.setDateModified(now);
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
	public void updateTodo(Long id, @Valid Todo patchedTodo) {
		if (todoRepository.updateById(id, patchedTodo.getName(), patchedTodo.getDescription(), patchedTodo.getPhase(), new Date()) < 1) {
			log.warn("No Todos were updated with id {{}}", id);
		}
	}
}
