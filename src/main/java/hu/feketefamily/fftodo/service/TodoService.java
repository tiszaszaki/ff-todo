package hu.feketefamily.fftodo.service;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.api.AddTodoRequest;
import hu.feketefamily.fftodo.model.api.FetchTaskResponse;
import hu.feketefamily.fftodo.model.api.FetchTodoResponse;
import hu.feketefamily.fftodo.model.api.TodoPhaseNameResponse;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
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

	private FetchTodoResponse buildFetchTodoResponse(Todo todo)
	{
		return FetchTodoResponse.builder()
			.id(todo.getId())
			.name(todo.getName())
			.description(todo.getDescription())
			.phase(todo.getPhase())
			.dateCreated(todo.getDateCreated())
			.dateModified(todo.getDateModified())
			.deadline(todo.getDeadline())
			.boardId(todo.getBoard().getId())
			.build();
	}

	private FetchTaskResponse buildFetchTaskResponse(Task task)
	{
		return FetchTaskResponse.builder()
			.id(task.getId())
			.name(task.getName())
			.done(task.getDone())
			.dateCreated(task.getDateCreated())
			.dateModified(task.getDateModified())
			.deadline(task.getDeadline())
			.todoId(task.getTodo().getId())
			.build();
	}

	public Todo getTodo(Long id) {
		Todo result = todoRepository.findById(id).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE(id, "")) );
		return result;
	}

	public FetchTodoResponse getTodoResponse(Long id) {
		Todo result = todoRepository.findById(id).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE(id, "")) );
		FetchTodoResponse response = buildFetchTodoResponse(result);
		Integer i = 0;
		log.info("Queried Todo by id {{}}: {}", id, response.toString());
		for (Task task : result.getTasks()) {
			FetchTaskResponse taskResponse = buildFetchTaskResponse(task);
			log.info("Queried Task #{} for Todo by id {{}}: {}", ++i, response.getId(), taskResponse.toString());
		}
		return response;
	}

	public Todo getTodoByName(String name) {
		Todo result = todoRepository.findByName(name).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE(0L, name)) );
		return result;
	}

	public FetchTodoResponse getTodoResponseByName(String name) {
		Todo result = todoRepository.findByName(name).orElseThrow(() -> new NotExistException(TODO_NOT_EXIST_MESSAGE(0L, name)) );
		FetchTodoResponse response = buildFetchTodoResponse(result);
		log.info("Queried Todo by name: {}", response.toString());
		return response;
	}

	public List<FetchTodoResponse> getTodos() {
		List<Todo> result = todoRepository.findAll();
		List<FetchTodoResponse> responseList = new ArrayList<>();
		Integer i = 0;
		for (Todo todo : result)
			responseList.add(buildFetchTodoResponse(todo));
		log.info("Queried {} Todo(s)", result.size());
		for (Todo t : result) {
			log.info("Todo #{}: {}", ++i, t.toString());
		}
		return responseList;
	}

	public List<Todo> getTodosFromBoard(Long id)
	{
		List<Todo> result = todoRepository.findByBoardId(id);
		return result;
	}

	public List<FetchTodoResponse> getTodosResponseFromBoard(Long id)
	{
		List<Todo> result = todoRepository.findByBoardId(id);
		List<FetchTodoResponse> responseList = new ArrayList<>();
		Integer i = 0;
		for (Todo todo : result)
			responseList.add(buildFetchTodoResponse(todo));
		log.info("Queried {} Todo(s) from Board by id {{}}", result.size(), id);
		for (Todo t : result) {
			log.info("Todo #{}: {}", ++i, t.toString());
		}
		return responseList;
	}

	public Todo addTodo(Long boardId, @Valid AddTodoRequest request) {
		Todo todo = Todo.builder()
			.name(request.getName())
			.description(request.getDescription())
			.phase(request.getPhase())
			.dateCreated(new Date())
			.dateModified(new Date())
			.deadline(request.getDeadline())
			.board(boardService.getBoard(boardId))
			.build();
		Todo newTodo = todoRepository.save(todo);

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
	public void updateTodo(Long id, @Valid Todo patchedTodo) {
		patchedTodo.setDateModified(new Date());
		if (todoRepository.updateById(id, patchedTodo.getName(), patchedTodo.getDescription(), patchedTodo.getPhase(),
			patchedTodo.getDateModified(), patchedTodo.getDeadline()) >= 1)
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
			Todo originalTodo = getTodo(id);
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

	public TodoPhaseNameResponse getTodoPhaseName(Integer idx) {
		String result = TodoCommon.getTodoPhaseName(idx);
		log.info("Querying phase name with index ({}) for all Todos: {}", idx, result);
		if (result.equals("")) {
			log.error("Queried empty result for phase name with index ({})", idx);
			throw new NotExistException(ErrorMessages.TODO_PHASE_NOT_EXIST(idx));
		}
		return TodoPhaseNameResponse.builder().phase(result).build();
	}
}
