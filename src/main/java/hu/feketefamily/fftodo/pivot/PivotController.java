package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(TodoCommon.pivotPath)
public class PivotController {
	@Autowired
	private PivotService pivotService;

	@GetMapping
	public ResponseEntity<Set<ReadinessRecord>> getBoardsReadiness() {
		return ResponseEntity.ok(pivotService.getBoardsReadiness());
	}

	@GetMapping
	public ResponseEntity<Set<ReadinessRecord>> getTodosReadiness() {
		return ResponseEntity.ok(pivotService.getTodosReadiness());
	}
}
