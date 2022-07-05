package hu.feketefamily.fftodo.service;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.api.AddTaskRequest;
import hu.feketefamily.fftodo.model.api.FetchTaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.repository.TaskRepository;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
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

	private FetchTaskResponse buildFetchTaskResponse(Task task)
	{
		return FetchTaskResponse.builder()
			.id(task.getId())
			.name(task.getName())
			.done(task.getDone())
			.deadline(task.getDeadline())
			.todoId(task.getTodo().getId())
			.build();
	}

	public List<FetchTaskResponse> getTasks() {
		List<Task> result = taskRepository.findAll();
		List<FetchTaskResponse> responseList = new ArrayList<>();
		Integer i = 0;
		for (Task task : result)
			responseList.add(buildFetchTaskResponse(task));
		log.info("Queried {} Task(s)", result.size());
		for (Task t : result) {
			log.info("Task #{}: {}", ++i, t.toString());
		}
		return responseList;
	}

	public List<Task> getTasksFromTodo(Long todoId)
	{
		List<Task> result = taskRepository.findByTodoId(todoId);
		return result;
	}

	public List<FetchTaskResponse> getTasksResponseFromTodo(Long todoId, Boolean logPerTask)
	{
		List<Task> result = taskRepository.findByTodoId(todoId);
		List<FetchTaskResponse> responseList = new ArrayList<>();
		for (Task task : result)
			responseList.add(buildFetchTaskResponse(task));
		log.info("Queried {} Task(s) from Todo by id {{}}", result.size(), todoId);
		if (logPerTask) {
			Integer i = 0;
			for (Task t : result) {
				log.info("Task #{}: {}", ++i, t.toString());
			}
		}
		return responseList;
	}

	public Task getTask(Long id) {
		Task result=taskRepository.findById(id).orElseThrow(() -> new NotExistException(TASK_NOT_EXIST_MESSAGE(id, "")) );
		return result;
	}

	public Task addTask(Long todoId, AddTaskRequest request) {
		Task task = Task.builder()
			.name(request.getName())
			.done(request.getDone())
			.todo(todoService.getTodo(todoId))
			.dateCreated(new Date())
			.dateModified(new Date())
			.deadline(request.getDeadline())
			.build();
		Task newTask = taskRepository.save(task);

		log.info("Saved new Task for Todo with id {{}}: {{}}", todoId, newTask.toString());
		return newTask;
	}

	public void removeTask(Long id) {
		if (taskRepository.existsById(id)) {
			log.info("Deleting Task with id: {{}}", id);
			try {
				Task tempTask = getTask(id);
				Long todoId = tempTask.getTodo().getId();
			} catch (NotExistException e) { }
			taskRepository.deleteById(id);
		} else {
			log.warn("No Tasks were deleted with id {{}}", id);
		}
	}

	@Transactional
	public void updateTask(Long id, @Valid Task patchedTask) {
		if (taskRepository.updateById(id, patchedTask.getName(), patchedTask.getDone(), patchedTask.getDeadline(), new Date()) >= 1)
		{
			Task tempTask=getTask(id);
			Long todoId=tempTask.getTodo().getId();
			log.info("Successfully updated Task with id {{}}: {}", id, patchedTask.toString());
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

		return temp_count;
	}

	public Integer getNameMaxLength() {
		Integer maxLength= TodoCommon.maxTaskNameLength;
		log.info("Querying maximum name length setting for all Tasks: {}", maxLength);
		return maxLength;
	}
}
