package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(TodoCommon.pivotPath)
public class PivotController {
	@Autowired
	private PivotService pivotService;

	@GetMapping(TodoCommon.pivotLabel1)
	public ResponseEntity<PivotResponse> getBoardsReadiness() {
		return ResponseEntity.ok(pivotService.getBoardsReadiness());
	}

	@GetMapping(TodoCommon.pivotLabel2)
	public ResponseEntity<PivotResponse> getTodosReadiness() {
		return ResponseEntity.ok(pivotService.getTodosReadiness());
	}

	@GetMapping(TodoCommon.pivotLabel3)
	public ResponseEntity<PivotResponse> getBoardsLatestUpdate() {
		return ResponseEntity.ok(pivotService.getBoardsLatestUpdate());
	}

	@GetMapping(TodoCommon.pivotLabel4)
	public ResponseEntity<PivotResponse> getTodosLatestUpdate() {
		return ResponseEntity.ok(pivotService.getTodosLatestUpdate());
	}
}
