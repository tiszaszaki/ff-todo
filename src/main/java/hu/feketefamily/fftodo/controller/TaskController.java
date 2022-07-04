package hu.feketefamily.fftodo.controller;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.api.FetchTaskResponse;
import hu.feketefamily.fftodo.model.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hu.feketefamily.fftodo.service.TaskService;

import java.util.List;

@RestController
@RequestMapping(TodoCommon.taskPath)
public class TaskController {

	@Autowired
	private TaskService taskService;

	@GetMapping
	public ResponseEntity<List<FetchTaskResponse>> getTasks() {
		return ResponseEntity.ok(taskService.getTasks());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeTask(@PathVariable Long id) {
		taskService.removeTask(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody Task patchedTask) {
		taskService.updateTask(id, patchedTask);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/name-max-length")
	public ResponseEntity<Integer> getNameMaxLength() {
		return ResponseEntity.ok(taskService.getNameMaxLength());
	}
}
