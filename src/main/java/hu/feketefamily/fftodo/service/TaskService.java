package hu.feketefamily.fftodo.service;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.entity.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;

import java.util.List;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TASK_NOT_EXIST_MESSAGE;

@Log4j2
@Service
@Validated
public class TaskService {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskRepository taskRepository;

	public List<Task> getTasks(Boolean logPerTask) {
		List<Task> result = taskRepository.findAll();
		log.info("Queried {} Task(s)", result.size());
		if (logPerTask) {
			Integer i = 0;
			for (Task t : result) {
				log.info("Task #{}: {}", ++i, t.toString());
			}
		}
		return result;
	}

	public List<Task> getTasksFromTodo(Long todoId, Boolean logPerTask)
	{
		List<Task> result = taskRepository.findByTodoId(todoId);
		log.info("Queried {} Task(s) from Todo with id {{}}", result.size(), todoId);
		if (logPerTask) {
			Integer i = 0;
			for (Task t : result) {
				log.info("Task #{}: {}", ++i, t.toString());
			}
		}
		return result;
	}
	public Task getTask(Long id) {
		return taskRepository.findById(id).orElseThrow(() -> new NotExistException(TASK_NOT_EXIST_MESSAGE) );
	}

	public Task addTask(Long todoId, Task task) {
		Task newTask;
		task.setTodo(todoService.getTodo(todoId, false));
		todoService.updateTodoDate(todoId);
		newTask = taskRepository.save(task);
		log.info("Saved new Task for Todo with id {{}}: {{}}", todoId, newTask.toString());
		return newTask;
	}

	public void removeTask(Long id) {
		if (taskRepository.existsById(id)) {
			log.info("Deleting Task with id: {{}}", id);
			try {
				Task tempTask = getTask(id);
				Long todoId = tempTask.getTodo().getId();
				todoService.updateTodoDate(todoId);
			} catch (NotExistException e) { }
			taskRepository.deleteById(id);
		} else {
			log.warn("No Tasks were deleted with id {{}}", id);
		}
	}

	@Transactional
	public void updateTask(Long id, @Valid Task patchedTask) {
		if (taskRepository.updateById(id, patchedTask.getName(), patchedTask.getDone(), patchedTask.getDeadline()) >= 1)
		{
			Task tempTask=getTask(id);
			Long todoId=tempTask.getTodo().getId();
			log.info("Successfully updated Task with id {{}}: {}", id, patchedTask.toString());
			todoService.updateTodoDate(todoId);
		}
		else
		{
			log.warn("No Tasks were updated with id {{}}", id);
		}
	}

	@Transactional
	public int removeAllTasksFromTodo(Long id) {
		int temp_count=taskRepository.deleteByTodoId(id);

		if (temp_count > 0)
			log.info("Successfully deleted {} Task(s) from Todo with id {{}}", temp_count, id);
		else
			log.warn("No Tasks were deleted from Todo with id {{}}", id);

		todoService.updateTodoDate(id);

		return temp_count;
	}

	public Integer getNameMaxLength() {
		Integer maxLength= TodoCommon.maxTaskNameLength;
		log.info("Querying maximum name length setting for all Tasks: {}", maxLength);
		return maxLength;
	}
}
