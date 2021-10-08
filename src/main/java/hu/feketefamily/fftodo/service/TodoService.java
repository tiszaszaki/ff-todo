package hu.feketefamily.fftodo.service;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
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
	private BoardService boardService;

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private TaskRepository taskRepository;

	public final Integer phaseMin= TodoCommon.phaseMin;
	public final Integer phaseMax= TodoCommon.phaseMax;

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

	public List<Todo> getTodosFromBoard(Long id)
	{
		List<Todo> result = todoRepository.findByBoardId(id);
		log.info("Queried " + result.size() + " Todos from Board with id {{}}", id);
		return result;
	}

	public List<Todo> getTodosSorted(Sort.Direction dir, String propName)
	{
		List<Todo> result = todoRepository.findAll(Sort.by(dir, propName));
		log.info("Queried " + result.size() + " sorted ({},'{}') Todos", dir, propName);
		return result;
	}

	public Todo addTodo(Long boardId, @Valid Todo todo) {
		Calendar dateCalc=Calendar.getInstance();
		Date now=new Date();

		dateCalc.setTime(now);

		todo.setDateCreated(now);

		//dateCalc.add(Calendar.DAY_OF_MONTH, -5);
		todo.setDateModified(dateCalc.getTime());

		todo.setBoard(boardService.getBoard(boardId));

		log.info("Saving Todo for Board with id {{}}: {{}}", boardId, todo.toString());
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
	public Long removeAllTodos(Long boardId) {
		long temp_count=todoRepository.findByBoardId(boardId).size();

		if (temp_count > 0) {
			log.info("Deleting all Todos from Board with id {{}}...", boardId);
			todoRepository.deleteByBoardId(boardId);
		} else {
			log.warn("No Todos were deleted from Board with id {{}}...", boardId);
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
			isValidPhase &= ((oldPhase >= phaseMin) && (oldPhase <= phaseMax));
			isValidPhase &= ((newPhase >= phaseMin) && (newPhase <= phaseMax));
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
		if (todoRepository.updateById(id, patchedTodo.getName(), patchedTodo.getDescription(), patchedTodo.getPhase(),
			new Date(), patchedTodo.getDeadline()) >= 1)
		{
			log.info("Successfully updated Todo with id {{}}: {}", id, patchedTodo);
		}
		else
		{
			log.warn("No Todos were updated with id {{}}", id);
		}
	}

	@Transactional
	public void cloneTodo(Long id, Integer phase, Long boardId) {
		if (todoRepository.cloneById(id, phase, boardId, new Date()) >= 1)
		{
			Todo tempTodo = getTodo(id);
			Integer results;

			log.info("Successfully cloned Todo with id {{}} to phase {} on Board with id {{}}", id, phase, boardId);

			if ((results = taskRepository.cloneByTodoId(id, tempTodo.getName())) >= 1)
			{
				log.info("Successfully cloned {} Tasks from Todo with id {{}}", results, id);
			}
			else
			{
				log.warn("No Tasks were cloned with from Todo with id {{}}", id);
			}
		}
		else
		{
			log.warn("No Todos were cloned with id {{}}", id);
		}
	}

	public Integer getDescriptionMaxLength() {
		Integer maxLength=TodoCommon.maxTodoDescriptionLength;
		log.info("Querying maximum description length setting for all Todos: {}", maxLength);
		return maxLength;
	}

	public List<Integer> getTodoPhaseRange() {
		List<Integer> results=new ArrayList<>();
		results.add(TodoCommon.phaseMin);
		results.add(TodoCommon.phaseMax);
		log.info("Querying phase range setting for all Todos: {}", results.toString());
		return results;
	}
}
