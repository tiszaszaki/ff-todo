package hu.feketefamily.fftodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.feketefamily.fftodo.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeTask(@PathVariable Long id) {
		taskService.removeTask(id);
		return ResponseEntity.ok().build();
	}
}
