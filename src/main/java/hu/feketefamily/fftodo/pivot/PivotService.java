package hu.feketefamily.fftodo.pivot;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Set;

@Log4j2
@Service
@Validated
public class PivotService {
	@Autowired
	private ReadinessPivotRepository readinessPivotRepository;

	public PivotResponse resultReadinessPivot(Set<ReadinessRecord> records)
	{
		var results =
			PivotResponse.builder()
				.records(Collections.singleton(records))
				.fields(PivotResponse.ExtractFieldsFromType(ReadinessRecord.class))
				.fieldOrder(ReadinessRecord.fieldOrder()).build();
		var tempFields = results.getFields();
		var tempRoles = ReadinessRecord.fieldRoles();
		for (var f : results.getFieldOrder())
		{
			var oldVal = tempFields.get(f).trim();
			var newVal = oldVal + ',' + tempRoles.get(f).trim();
			tempFields.replace(f, oldVal, newVal);
		}
		return results;
	}
	public PivotResponse getBoardsReadiness() {
		var records=readinessPivotRepository.getAllBoardsReadiness();
		log.info("Fetched {} Board(s) with readiness", records.size());
		return resultReadinessPivot(records);
	}

	public PivotResponse getTodosReadiness() {
		var records=readinessPivotRepository.getAllTodosReadiness();
		log.info("Fetched {} Todo(s) with readiness", records.size());
		return resultReadinessPivot(records);
	}
}
