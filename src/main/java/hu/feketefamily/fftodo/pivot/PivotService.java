package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
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

	public PivotResponse resultReadinessPivot(Set<ReadinessRecord> records, String queryLabel)
	{
		var results = new PivotResponse<ReadinessRecord>();
		var tempFields = results.extractFieldsFromType(ReadinessRecord.class);
		var tempFieldOrder = ReadinessRecord.fieldOrder();
		var tempRoles = ReadinessRecord.fieldRoles();
		if (queryLabel.equals("")) queryLabel = "default-pivot-query";
		results.setRecords(records);
		results.setFields(tempFields);
		results.setFieldOrder(tempFieldOrder);
		log.info("Created response object for pivot query with ID '{}'", queryLabel);
		log.info("Number of fields in order: {}, number of roles: {}", tempFieldOrder.size(), tempRoles.size());
		for (var f : tempFieldOrder)
		{
			PivotResponseFieldPair tempEntry = new PivotResponseFieldPair("","");
			var oldVal = "";
			for (var e : tempFields)
			{
				if (e.key == f) {
					tempEntry = e;
					oldVal = e.value;
					break;
				}
			}
			var tempVal = tempRoles.get(f);
			if (!tempVal.equals("")) {
				var newVal = oldVal + ',' + tempVal;
				tempFields.remove(tempEntry);
				tempEntry.setKey(f);
				tempEntry.setValue(newVal);
				tempFields.add(tempEntry);
			}
			log.info("Iterated ReadinessRecord field: '{}', '{}', '{}'", f, oldVal, tempVal);
		}
		for (var r : results.getRecords())
			r.setDoneTaskPercent((r.getDoneTaskCount() + 0.0) / r.getTaskCount());
		return results;
	}
	public PivotResponse getBoardsReadiness() {
		var records=readinessPivotRepository.getAllBoardsReadiness();
		log.info("Fetched {} Board(s) with readiness", records.size());
		return resultReadinessPivot(records, TodoCommon.pivotLabel1);
	}

	public PivotResponse getTodosReadiness() {
		var records=readinessPivotRepository.getAllTodosReadiness();
		log.info("Fetched {} Todo(s) with readiness", records.size());
		return resultReadinessPivot(records, TodoCommon.pivotLabel2);
	}
}
