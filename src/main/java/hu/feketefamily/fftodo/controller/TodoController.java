package hu.feketefamily.fftodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.TodoService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/todo")
public class TodoController {

	@Autowired
	private TodoService todoService;

	@GetMapping
	public ResponseEntity<List<Todo>> getTodos() {
		return ResponseEntity.ok(todoService.getTodos());
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
}
