package hu.feketefamily.fftodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;

@Log4j2
@Service
@Validated
public class TaskService {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskRepository taskRepository;

	public void addTask(Long todoId, Task task) {
		task.setTodo(todoService.getTodo(todoId));
		log.info("Saving Task: {{}}", task.toString());
		taskRepository.save(task);
	}

	public void removeTask(Long id) {
		if (taskRepository.existsById(id)) {
			log.info("Deleting Task with id: {{}}", id);
			taskRepository.deleteById(id);
		} else {
			log.warn("Deleting non-existing Task with id {{}}", id);
		}
	}

	@Transactional
	public void updateTask(Long id, @Valid Task patchedTask) {
		if (taskRepository.updateById(id, patchedTask.getName(), patchedTask.getDone()) < 1) {
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

		return temp_count;
	}
}
