package hu.feketefamily.fftodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/todo")
public class TodoController {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskService taskService;

	@GetMapping
	public ResponseEntity<List<Todo>> getTodos() {
		return ResponseEntity.ok(todoService.getTodos());
	}

	@GetMapping("/sorted/{dir}/{propName}")
	public ResponseEntity<List<Todo>> getTodosSorted(@PathVariable("dir") String dirStr, @PathVariable String propName) {
		Sort.Direction dir=Sort.Direction.valueOf(dirStr.toUpperCase());
		return ResponseEntity.ok(todoService.getTodosSorted(dir, propName));
	}

	@GetMapping("/{id}/tasks")
	public ResponseEntity<List<Task>> getTasksFromTodo(@PathVariable Long id)
	{
		return ResponseEntity.ok(taskService.getTasksFromTodo(id));
	}

	@GetMapping("/{id}/tasks/sorted/{dir}/{propName}")
	public ResponseEntity<List<Task>> getTasksFromTodo(@PathVariable Long id, @PathVariable("dir") String dirStr, @PathVariable String propName)
	{
		Sort.Direction dir=Sort.Direction.valueOf(dirStr.toUpperCase());
		return ResponseEntity.ok(taskService.getTasksSortedFromTodo(id, dir, propName));
	}

	@PutMapping
	public ResponseEntity<Todo> addTodo(@RequestBody Todo todo) {
		return ResponseEntity.ok(todoService.addTodo(todo));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeTodo(@PathVariable Long id) {
		todoService.removeTodo(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/clear")
	public ResponseEntity<Long> removeAllTodos() {
		return ResponseEntity.ok(todoService.removeAllTodos());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateTodo(@PathVariable Long id, @RequestBody Todo patchedTodo) {
		todoService.updateTodo(id, patchedTodo);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/task")
	public ResponseEntity<Void> addTask(@PathVariable Long id, @RequestBody Task task) {
		taskService.addTask(id, task);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/task/clear")
	public ResponseEntity<Integer> removeAllTasks(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.removeAllTasksFromTodo(id));
	}
}
