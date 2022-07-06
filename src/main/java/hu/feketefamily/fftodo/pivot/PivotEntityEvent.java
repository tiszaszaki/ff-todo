package hu.feketefamily.fftodo.pivot;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PivotEntityEvent {
	private LatestUpdateRecord.LatestUpdateEvent type;
	private Date time;
	private long affectedId;
	private String affectedName;
}
