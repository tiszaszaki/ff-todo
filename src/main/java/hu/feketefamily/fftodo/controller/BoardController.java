package hu.feketefamily.fftodo.controller;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.entity.Todo;
import hu.feketefamily.fftodo.service.BoardService;
import hu.feketefamily.fftodo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(TodoCommon.boardPath)
public class BoardController {
	@Autowired
	private BoardService boardService;

	@Autowired
	private TodoService todoService;

	@GetMapping
	public ResponseEntity<Set<Long>> getBoard() {
		return ResponseEntity.ok(boardService.getAllBoardsId());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Board> getBoard(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.getBoard(id));
	}

	@GetMapping("/{id}/todos")
	public ResponseEntity<List<Todo>> getTodosFromBoard(@PathVariable Long id)
	{
		return ResponseEntity.ok(todoService.getTodosFromBoard(id));
	}

	@PutMapping("/{id}/todo")
	public ResponseEntity<Todo> addTodo(@PathVariable Long id, @RequestBody Todo todo) {
		todoService.addTodo(id, todo);
		return ResponseEntity.ok().build();
	}
}
