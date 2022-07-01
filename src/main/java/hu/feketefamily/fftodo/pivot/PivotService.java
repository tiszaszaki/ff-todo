package hu.feketefamily.fftodo.pivot;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Log4j2
@Service
@Validated
public class PivotService {
	@Autowired
	private ReadinessPivotRepository readinessPivotRepository;

	public Set<ReadinessRecord> getBoardsReadiness() {
		var results=readinessPivotRepository.getAllBoardsReadiness();
		log.info("Fetched {} Board(s) with readiness", results.size());
		return results;
	}

	public Set<ReadinessRecord> getTodosReadiness() {
		var results=readinessPivotRepository.getAllTodosReadiness();
		log.info("Fetched {} Todo(s) with readiness", results.size());
		return results;
	}
}
