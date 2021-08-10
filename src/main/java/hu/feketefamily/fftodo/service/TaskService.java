package hu.feketefamily.fftodo.service;

import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.entity.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static hu.feketefamily.fftodo.constants.ErrorMessages.TASK_NOT_EXIST_MESSAGE;
import static hu.feketefamily.fftodo.constants.ErrorMessages.TODO_NOT_EXIST_MESSAGE;

@Log4j2
@Service
@Validated
public class TaskService {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskRepository taskRepository;

	public Task getTask(Long id) {
		return taskRepository.findById(id).orElseThrow(() -> new NotExistException(TASK_NOT_EXIST_MESSAGE) );
	}

	public List<Task> getTasksFromTodo(Long id)
	{
		List<Task> result = taskRepository.findByTodoId(id);
		log.info("Queried " + result.size() + " Tasks from Todo with id {{}}", id);
		return result;
	}

	public List<Task> getTasksSortedFromTodo(Long id, Sort.Direction dir, String propName)
	{
		List<Task> result = taskRepository.findByTodoId(id, Sort.by(dir, propName));
		log.info("Queried " + result.size() + " sorted ({},'{}') Tasks from Todo with id {{}}", dir, propName, id);
		return result;
	}

	public void addTask(Long todoId, Task task) {
		Todo tempTodo=todoService.getTodo(todoId);
		task.setTodo(tempTodo);
		log.info("Saving Task: {{}}", task.toString());
		taskRepository.save(task);
		tempTodo.setDateModified(new Date());
		todoService.updateTodo(todoId, tempTodo);
	}

	public void removeTask(Long id) {
		if (taskRepository.existsById(id)) {
			Task tempTask=getTask(id);
			Long todoId=tempTask.getTodo().getId();
			Todo tempTodo=todoService.getTodo(todoId);
			log.info("Deleting Task with id: {{}}", id);
			taskRepository.deleteById(id);
			tempTodo.setDateModified(new Date());
			todoService.updateTodo(todoId, tempTodo);
		} else {
			log.warn("Deleting non-existing Task with id {{}}", id);
		}
	}

	@Transactional
	public void updateTask(Long id, @Valid Task patchedTask) {
		if (taskRepository.updateById(id, patchedTask.getName(), patchedTask.getDone()) >= 1)
		{
			Task tempTask=getTask(id);
			Long todoId=tempTask.getTodo().getId();
			Todo tempTodo=todoService.getTodo(todoId);
			log.info("Successfully updated Task with id {{}}: {}", id, patchedTask.toString());
			tempTodo.setDateModified(new Date());
			todoService.updateTodo(todoId, tempTodo);
		}
		else
		{
			log.warn("No Tasks were updated with id {{}}", id);
		}
	}

	@Transactional
	public int removeAllTasksFromTodo(Long id) {
		int temp_count=taskRepository.deleteByTodoId(id);
		Todo tempTodo=todoService.getTodo(id);

		if (temp_count > 0)
			log.info("Successfully deleted {} Task(s) from Todo with id {{}}", temp_count, id);
		else
			log.warn("No Tasks were deleted from Todo with id {{}}", id);

		tempTodo.setDateModified(new Date());
		todoService.updateTodo(id, tempTodo);

		return temp_count;
	}
}
