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
	public ResponseEntity<Set<Long>> getBoardIds() {
		return ResponseEntity.ok(boardService.getBoardsId());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Board> getBoard(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.getBoard(id));
	}

	@GetMapping("/{id}/todos")
	public ResponseEntity<List<Todo>> getTodosFromBoard(@PathVariable Long id)
	{
		return ResponseEntity.ok(todoService.getTodosFromBoard(id, true));
	}

	@PutMapping
	public ResponseEntity<Board> addBoard(@RequestBody Board board) {
		return ResponseEntity.ok(boardService.addBoard(board));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeBoard(@PathVariable Long id) {
		boardService.removeBoard(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBoard(@PathVariable Long id, @RequestBody Board patchedBoard) {
		boardService.updateBoard(id, patchedBoard);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/todo")
	public ResponseEntity<Todo> addTodo(@PathVariable Long id, @RequestBody Todo todo) {
		return ResponseEntity.ok(todoService.addTodo(id, todo));
	}

	@DeleteMapping("/{id}/todo/clear")
	public ResponseEntity<Long> removeAllTodos(@PathVariable Long id) {
		return ResponseEntity.ok(todoService.removeAllTodos(id));
	}

	@GetMapping("/name-max-length")
	public ResponseEntity<Integer> getNameMaxLength() {
		return ResponseEntity.ok(boardService.getNameMaxLength());
	}
	@GetMapping("/description-max-length")
	public ResponseEntity<Integer> getDescriptionMaxLength() {
		return ResponseEntity.ok(boardService.getDescriptionMaxLength());
	}
	@GetMapping("/author-max-length")
	public ResponseEntity<Integer> getAuthorMaxLength() {
		return ResponseEntity.ok(boardService.getAuthorMaxLength());
	}

	@GetMapping("/{id}/readonly-todos")
	public ResponseEntity<Boolean> getBoardReadonlyTodosSetting(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.isReadonlyTodos(id));
	}

	@PatchMapping("/{id}/readonly-todos/{readonly}")
	public ResponseEntity<Void> setBoardReadonlyTodosSetting(@PathVariable Long id, @PathVariable Boolean readonly) {
		boardService.setReadonlyTodos(id, readonly);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/readonly-tasks")
	public ResponseEntity<Boolean> getBoardReadonlyTasksSetting(@PathVariable Long id) {
		return ResponseEntity.ok(boardService.isReadonlyTasks(id));
	}

	@PatchMapping("/{id}/readonly-tasks/{readonly}")
	public ResponseEntity<Void> setBoardReadonlyTaskSetting(@PathVariable Long id, @PathVariable Boolean readonly) {
		boardService.setReadonlyTasks(id, readonly);
		return ResponseEntity.ok().build();
	}
}
