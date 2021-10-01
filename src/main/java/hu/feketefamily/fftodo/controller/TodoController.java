package hu.feketefamily.fftodo.controller;

import hu.feketefamily.fftodo.constants.TodoCommon;
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
@RequestMapping(TodoCommon.todoPath)
public class TodoController {

	@Autowired
	private TodoService todoService;

	@Autowired
	private TaskService taskService;

	@GetMapping
	public ResponseEntity<List<Todo>> getTodos() {
		return ResponseEntity.ok(todoService.getTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
		return ResponseEntity.ok(todoService.getTodo(id));
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
	public ResponseEntity<List<Task>> getTasksSortedFromTodo(@PathVariable Long id, @PathVariable("dir") String dirStr, @PathVariable String propName)
	{
		Sort.Direction dir=Sort.Direction.valueOf(dirStr.toUpperCase());
		return ResponseEntity.ok(taskService.getTasksSortedFromTodo(id, dir, propName));
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

	@PatchMapping("/{id}/clone/{phase}")
	public ResponseEntity<Void> cloneTodo(@PathVariable Long id, @PathVariable Integer phase) {
		todoService.cloneTodo(id, phase);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/shift/{dir}")
	public ResponseEntity<Void> shiftTodo(@PathVariable Long id, @PathVariable("dir") String dirStr) {
		TodoService.ShiftDirection dir=TodoService.ShiftDirection.valueOf(dirStr.toUpperCase());
		todoService.shiftTodo(id, dir);
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

	@GetMapping("/description-max-length")
	public ResponseEntity<Integer> getDescriptionMaxLength() {
		return ResponseEntity.ok(todoService.getDescriptionMaxLength());
	}

	@GetMapping("/phase-val-range")
	public ResponseEntity< List<Integer> > getTodoPhaseRange() {
		return ResponseEntity.ok(todoService.getTodoPhaseRange());
	}
}
