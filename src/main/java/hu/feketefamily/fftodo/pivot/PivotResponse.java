package hu.feketefamily.fftodo.pivot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class PivotResponse<T> {
	private Set<PivotResponseFieldPair> fields;
	private Set<PivotResponseFieldPair> fieldDisplay;
	private List<String> fieldOrder;
	private Set<T> records;
}
