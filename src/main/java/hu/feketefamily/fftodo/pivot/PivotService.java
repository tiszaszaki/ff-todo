package hu.feketefamily.fftodo.pivot;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.model.repository.BoardRepository;
import hu.feketefamily.fftodo.model.repository.TodoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

@Log4j2
@Service
@Validated
public class PivotService {
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private TodoRepository todoRepository;

	private PivotResponse prepareResultReadinessPivot(Set records, String queryLabel)
	{
		var results = new PivotResponse<ReadinessRecord>();
		var tempFields = results.extractFieldsFromType(ReadinessRecord.class);
		var tempFieldOrder = ReadinessRecord.fieldOrder();
		var tempRoles = ReadinessRecord.fieldRoles();
		if (queryLabel.equals("")) queryLabel = "default-pivot-query";
		results.setRecords(records);
		results.setFields(tempFields);
		results.setFieldOrder(tempFieldOrder);
		results.setFieldDisplay(ReadinessRecord.fieldDisplay());
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
		}
		for (var r : results.getRecords()) {
			if (r.getTaskCount() != 0)
				r.setDoneTaskPercent((r.getDoneTaskCount() + 0.0) / r.getTaskCount());
			else
				r.setDoneTaskPercent(-1.0);
		}
		log.info("Created readiness response object for pivot query with ID '{}'", queryLabel);
		log.debug("");
		return results;
	}

	private PivotResponse prepareResultLatestUpdatePivot(Set records, String queryLabel)
	{
		var results = new PivotResponse<LatestUpdateRecord>();
		var tempFields = results.extractFieldsFromType(LatestUpdateRecord.class);
		var tempFieldOrder = LatestUpdateRecord.fieldOrder();
		var tempRoles = LatestUpdateRecord.fieldRoles();
		if (queryLabel.equals("")) queryLabel = "default-pivot-query";
		results.setRecords(records);
		results.setFields(tempFields);
		results.setFieldOrder(tempFieldOrder);
		results.setFieldDisplay(LatestUpdateRecord.fieldDisplay());
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
		}
		log.info("Created latest update response object for pivot query with ID '{}'", queryLabel);
		log.debug("");
		return results;
	}

	public PivotResponse getBoardsReadiness() {
		var temp=boardRepository.findAll();
		var records = new HashSet<ReadinessRecord>();
		for (var e : temp)
		{
			records.add(ReadinessRecord.builder()
				.id(e.getId())
				.name(e.getName())
				.doneTaskCount(e.getDoneTaskCount())
				.taskCount(e.getTaskCount())
				.build());
		}
		log.info("Fetched {} Board(s) with readiness ({})", records.size(), TodoCommon.pivotLabel1);
		return prepareResultReadinessPivot(records, TodoCommon.pivotLabel1);
	}
	public PivotResponse getTodosReadiness() {
		var temp=todoRepository.findAll();
		var records = new HashSet<ReadinessRecord>();
		for (var e : temp)
		{
			records.add(ReadinessRecord.builder()
				.id(e.getId())
				.name(e.getName())
				.doneTaskCount(e.getDoneTaskCount())
				.taskCount(e.getTaskCount())
				.build());
		}
		log.info("Fetched {} Todo(s) with readiness ({})", records.size(), TodoCommon.pivotLabel2);
		return prepareResultReadinessPivot(records, TodoCommon.pivotLabel2);
	}

	public PivotResponse getBoardsLatestUpdate() {
		var temp=boardRepository.findAll();
		var records = new HashSet<LatestUpdateRecord>();
		for (var e : temp)
		{
			records.add(LatestUpdateRecord.builder()
				.id(e.getId())
				.name(e.getName())
				.latestUpdated(e.getLatestUpdated())
				.latestEvent(e.getLatestEvent().toString())
				.affectedId(e.getAffectedId())
				.affectedName(e.getAffectedName())
				.build());
		}
		log.info("Fetched {} Board(s) with latest update ({})", records.size(), TodoCommon.pivotLabel3);
		return prepareResultLatestUpdatePivot(records, TodoCommon.pivotLabel3);
	}
	public PivotResponse getTodosLatestUpdate() {
		var temp=todoRepository.findAll();
		var records = new HashSet<LatestUpdateRecord>();
		for (var e : temp)
		{
			records.add(LatestUpdateRecord.builder()
				.id(e.getId())
				.name(e.getName())
				.latestUpdated(e.getLatestUpdated())
				.latestEvent(e.getLatestEvent().toString())
				.affectedId(e.getAffectedId())
				.affectedName(e.getAffectedName())
				.build());
		}
		log.info("Fetched {} Todo(s) with latest update ({})", records.size(), TodoCommon.pivotLabel4);
		return prepareResultLatestUpdatePivot(records, TodoCommon.pivotLabel4);
	}
}
