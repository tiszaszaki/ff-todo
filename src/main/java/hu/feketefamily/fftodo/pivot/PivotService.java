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

	private PivotResponse<ReadinessRecord> prepareResultReadinessPivot(Set<ReadinessRecord> records, String queryLabel)
	{
		var results = new PivotResponse<ReadinessRecord>();
		if (queryLabel.equals("")) queryLabel = "default-pivot-query";
		results.setRecords(records);
		results.setFields(PivotResponseTools.extractFieldsFromType(ReadinessRecord.class));
		results.setFieldOrder(PivotResponseTools.extractFieldOrderFromType(ReadinessRecord.class));
		results.setFieldDisplay(PivotResponseTools.extractFieldDisplayFromType(ReadinessRecord.class));
		for (var r : results.getRecords())
			r.setDoneTaskPercent(ReadinessRecord.GetPercent(r.getDoneTaskCount(), r.getTaskCount()));
		log.info("Created readiness response object for pivot query with ID '{}'", queryLabel);
		log.debug("");
		return results;
	}

	private PivotResponse<LatestUpdateRecord> prepareResultLatestUpdatePivot(Set<LatestUpdateRecord> records, String queryLabel)
	{
		var results = new PivotResponse<LatestUpdateRecord>();
		if (queryLabel.equals("")) queryLabel = "default-pivot-query";
		results.setRecords(records);
		results.setFields(PivotResponseTools.extractFieldsFromType(LatestUpdateRecord.class));
		results.setFieldOrder(PivotResponseTools.extractFieldOrderFromType(LatestUpdateRecord.class));
		results.setFieldDisplay(PivotResponseTools.extractFieldDisplayFromType(LatestUpdateRecord.class));
		log.info("Created latest update response object for pivot query with ID '{}'", queryLabel);
		log.debug("");
		return results;
	}

	public PivotResponse<ReadinessRecord> getBoardsReadiness() {
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
	public PivotResponse<ReadinessRecord> getTodosReadiness() {
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

	public PivotResponse<LatestUpdateRecord> getBoardsLatestUpdate() {
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
	public PivotResponse<LatestUpdateRecord> getTodosLatestUpdate() {
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
