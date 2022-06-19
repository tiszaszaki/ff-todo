package hu.feketefamily.fftodo.controller;

import hu.feketefamily.fftodo.constants.ErrorMessages;
import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.api.AddTaskRequest;
import hu.feketefamily.fftodo.model.api.TodoPhaseNameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import hu.feketefamily.fftodo.model.entity.Task;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.TaskService;
import hu.feketefamily.fftodo.service.TodoService;

@RestController
@RequestMapping(TodoCommon.todoPath)
public class TodoController {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskService taskService;

	@GetMapping
	public ResponseEntity<List<Todo>> getTodos() {
		return ResponseEntity.ok(todoService.getTodos(true));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
		return ResponseEntity.ok(todoService.getTodo(id, true));
	}

	@GetMapping("/{id}/tasks")
	public ResponseEntity<List<Task>> getTasksFromTodo(@PathVariable Long id)
	{
		return ResponseEntity.ok(taskService.getTasksFromTodo(id, true));
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<Todo> getTodoByName(@PathVariable String name) {
		return ResponseEntity.ok(todoService.getTodoByName(name));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeTodo(@PathVariable Long id) {
		todoService.removeTodo(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateTodo(@PathVariable Long id, @RequestBody Todo patchedTodo) {
		todoService.updateTodo(id, patchedTodo);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/clone/{phase}/{board}")
	public ResponseEntity<Todo> cloneTodo(@PathVariable Long id, @PathVariable Integer phase, @PathVariable("board") Long boardId) {
		return ResponseEntity.ok(todoService.cloneTodo(id, phase, boardId));
	}

	@PutMapping("/{id}/task")
	public ResponseEntity<Task> addTask(@PathVariable Long id, @RequestBody AddTaskRequest request) {
		return ResponseEntity.ok(taskService.addTask(id, request));
	}

	@DeleteMapping("/{id}/task/clear")
	public ResponseEntity<Integer> removeAllTasksFromTodo(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.removeAllTasksFromTodo(id));
	}

	@GetMapping("/name-max-length")
	public ResponseEntity<Integer> getNameMaxLength() {
		return ResponseEntity.ok(todoService.getNameMaxLength());
	}

	@GetMapping("/description-max-length")
	public ResponseEntity<Integer> getDescriptionMaxLength() {
		return ResponseEntity.ok(todoService.getDescriptionMaxLength());
	}

	@GetMapping("/phase-val-range")
	public ResponseEntity< List<Integer> > getTodoPhaseRange() {
		return ResponseEntity.ok(todoService.getTodoPhaseRange());
	}

	@GetMapping("/phase-name/{idx}")
	public ResponseEntity<TodoPhaseNameResponse> getTodoPhaseName(@PathVariable Integer idx) {
		return ResponseEntity.ok(todoService.getTodoPhaseName(idx));
	}
}
