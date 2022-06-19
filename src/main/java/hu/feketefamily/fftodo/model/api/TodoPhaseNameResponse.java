package hu.feketefamily.fftodo.model.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoPhaseNameResponse {
	private String phase;
}
