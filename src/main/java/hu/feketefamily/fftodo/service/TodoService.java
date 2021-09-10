package hu.feketefamily.fftodo.service;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Calendar;
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

	public final Integer phase_N=3;

	public enum ShiftDirection
	{
		LEFT,
		RIGHT
	};

	public Todo getTodo(Long id) {
		return todoRepository.findById(id).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE) );
	}

	public List<Todo> getTodos() {
		List<Todo> result = todoRepository.findAll();
		log.info("Queried " + result.size() + " Todos");
		return result;

	}

	public List<Todo> getTodosSorted(Sort.Direction dir, String propName)
	{
		List<Todo> result = todoRepository.findAll(Sort.by(dir, propName));
		log.info("Queried " + result.size() + " sorted ({},'{}') Todos", dir, propName);
		return result;
	}

	public Todo addTodo(@Valid Todo todo) {
		Calendar dateCalc=Calendar.getInstance();
		Date now=new Date();

		dateCalc.setTime(now);

		todo.setDateCreated(now);

		//dateCalc.add(Calendar.DAY_OF_MONTH, -5);
		todo.setDateModified(dateCalc.getTime());

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

	public Long removeAllTodos() {
		long temp_count=todoRepository.count();

		if (temp_count > 0) {
			log.info("Deleting all Todos...");
			todoRepository.deleteAll();
		} else {
			log.warn("No Todos were deleted.");
		}

		return temp_count;
	}

	@Transactional
	public void updateTodoDate(Long id)
	{
		Todo tempTodo=getTodo(id);
		tempTodo.setDateModified(new Date());
		log.info("Refreshing date modified for Todo with id {{}}", id);
		updateTodo(id, tempTodo);
	}

	@Transactional
	public void shiftTodo(Long id, ShiftDirection dir)
	{
		Todo tempTodo=getTodo(id);
		Integer oldPhase=tempTodo.getPhase(), newPhase=-1;
		Boolean isValidDir, isValidPhase=false;

		isValidDir = ((dir == ShiftDirection.LEFT) || (dir == ShiftDirection.RIGHT));

		switch (dir)
		{
			case LEFT: {
				newPhase = oldPhase - 1;
			} break;
			case RIGHT: {
				newPhase = oldPhase + 1;
			} break;
			default: break;
		};

		if (isValidDir)
		{
			isValidPhase = true;
			isValidPhase &= ((oldPhase >= 0) && (oldPhase < phase_N));
			isValidPhase &= ((newPhase >= 0) && (newPhase < phase_N));
		}

		if (isValidDir && isValidPhase) {
			String dirStr=String.valueOf(dir);
			tempTodo.setPhase(newPhase);
			log.info("Shifting Todo with id {{}} to the {}", id, dirStr.toLowerCase());
			updateTodo(id, tempTodo);
		}
		else
		{
			log.warn("Failed to shift Todo with id {{}}");
		}
	}

	@Transactional
	public void updateTodo(Long id, @Valid Todo patchedTodo) {
		if (todoRepository.updateById(id, patchedTodo.getName(), patchedTodo.getDescription(), patchedTodo.getPhase(), new Date()) >= 1)
		{
			log.info("Successfully updated Todo with id {{}}: {}", id, patchedTodo);
		}
		else
		{
			log.warn("No Todos were updated with id {{}}", id);
		}
	}
}
