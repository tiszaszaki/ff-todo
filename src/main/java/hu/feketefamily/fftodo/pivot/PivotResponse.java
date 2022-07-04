package hu.feketefamily.fftodo.pivot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class PivotResponse<T> {

	public static Set<PivotResponseFieldPair> extractFieldsFromType(Class c)
	{
		var res = new HashSet<PivotResponseFieldPair>();
		var fields = c.getFields();
		for (var f : fields) {
			var fieldName = f.getName();
			var fieldType= f.getType().getName();
			res.add(new PivotResponseFieldPair(fieldName, fieldType));
		}
		return res;
	}

	private Set<PivotResponseFieldPair> fields;
	private List<String> fieldOrder;
	private Set<T> records;
}
