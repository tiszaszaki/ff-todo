package hu.feketefamily.fftodo.service;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


	public Todo getTodo(Long id, Boolean logTaskList) {
		Todo result = todoRepository.findById(id).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE) );
		if (logTaskList)
		{
			Integer i = 0;
			for (Task t : result.getTasks()) {
				log.info("Task #{} for Todo with ID {{}}: {}", ++i, result.getId(), t.toString());
			}
		}
		return result;
	}

	public Todo getTodoByName(String name) {
		return todoRepository.findByName(name).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE) );
	}

	public List<Todo> getTodos(Boolean logPerTodo) {
		List<Todo> result = todoRepository.findAll();
		log.info("Queried {} Todo(s)", result.size());
		if (logPerTodo) {
			Integer i = 0;
			for (Todo t : result) {
				log.info("Todo #{}: {}", ++i, t.toString());
			}
		}
		return result;
	}

	public List<Todo> getTodosFromBoard(Long id, Boolean logPerTodo)
	{
		List<Todo> result = todoRepository.findByBoardId(id);
		log.info("Queried {} Todo(s) from Board with id {{}}", result.size(), id);
		if (logPerTodo) {
			Integer i = 0;
			for (Todo t : result) {
				log.info("Todo #{}: {}", ++i, t.toString());
			}
		}
		return result;
	}

	public Todo addTodo(Long boardId, @Valid Todo todo) {
		Calendar dateCalc=Calendar.getInstance();
		Date now=new Date(); Todo newTodo;

		dateCalc.setTime(now);
		todo.setDateCreated(now);
		todo.setDateModified(dateCalc.getTime());
		todo.setBoard(boardService.getBoard(boardId));

		newTodo = todoRepository.save(todo);

		log.info("Saved new Todo for Board with id {{}}: {{}}", boardId, newTodo.toString());

		return newTodo;
	}

	public void removeTodo(Long id) {
		if (todoRepository.existsById(id)) {
			log.info("Deleting Todo with id: {{}}", id);
			todoRepository.deleteById(id);
		} else {
			log.warn("No Todos were deleted with id {{}}", id);
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
		Todo tempTodo=getTodo(id, false);
		tempTodo.setDateModified(new Date());
		log.info("Refreshing date modified for Todo with id {{}}", id);
		updateTodo(id, tempTodo);
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
	public Todo cloneTodo(Long id, Integer phase, Long boardId) {
		Todo result = null;
		if (todoRepository.existsById(id)) {
			Todo originalTodo = getTodo(id, false);
			String originalTodoName = originalTodo.getName();
			Integer lengthOverrun = (originalTodoName.length() + TodoCommon.todoCloneSuffix.length()) - TodoCommon.maxTodoNameLength;
			if (lengthOverrun > 0) {
				Integer strTruncateIdx = TodoCommon.maxTodoNameLength / 2, lengthOverrunHalf;
				String modifiedOriginalTodoName = "";
				lengthOverrun += TodoCommon.fieldTruncateStr.length();
				lengthOverrunHalf = lengthOverrun / 2;
				modifiedOriginalTodoName += originalTodoName.substring(0, strTruncateIdx - lengthOverrunHalf);
				modifiedOriginalTodoName += TodoCommon.fieldTruncateStr;
				modifiedOriginalTodoName += originalTodoName.substring(strTruncateIdx + lengthOverrun - lengthOverrunHalf);
				originalTodo.setName(modifiedOriginalTodoName);
				log.warn("Truncated name of Todo with id {{}} from \"{}\" to \"{}\"", id, originalTodoName, modifiedOriginalTodoName);
			}
			if (todoRepository.cloneById(id, phase, boardId, new Date()) >= 1) {
				String clonedTodoName = originalTodo.getName() + TodoCommon.todoCloneSuffix;
				Todo clonedTodo = getTodoByName(clonedTodoName);
				Integer results;

				log.info("Successfully cloned Todo with id {{}} to phase {} on Board with id {{}}", id, phase, boardId);

				if ((results = taskRepository.cloneByTodoId(id, originalTodo.getName())) >= 1) {
					log.info("Successfully cloned {} Tasks from Todo with id {{}}", results, id);
				} else {
					log.warn("No Tasks were cloned with from Todo with id {{}}", id);
				}

				result = clonedTodo;
			}
		}
		if (result == null) {
			log.warn("No Todos were cloned with id {{}}", id);
		}
		return result;
	}

	public Integer getNameMaxLength() {
		Integer maxLength=TodoCommon.maxTodoNameLength;
		log.info("Querying maximum name length setting for all Todos: {}", maxLength);
		return maxLength;
	}

	public Integer getDescriptionMaxLength() {
		Integer maxLength=TodoCommon.maxTodoDescriptionLength;
		log.info("Querying maximum description length setting for all Todos: {}", maxLength);
		return maxLength;
	}

	public List<Integer> getTodoPhaseRange() {
		List<Integer> results=new ArrayList<>();
		results.add(TodoCommon.todoPhaseMin);
		results.add(TodoCommon.todoPhaseMax);
		log.info("Querying phase range setting for all Todos: {}", results.toString());
		return results;
	}

	public String getTodoPhaseName(Integer idx) {
		String result = TodoCommon.getTodoPhaseName(idx);
		if (result != "")
			log.info("Querying phase name with index ({}) for all Todos: {}", idx, result);
		else
			log.warn("Queried empty result for phase name with index ({})", idx);
		return result;
	}
}
