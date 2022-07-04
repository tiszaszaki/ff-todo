package hu.feketefamily.fftodo.pivot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

@Data
@NoArgsConstructor
public class PivotResponse<T> {

	@Retention(RetentionPolicy.RUNTIME)
	@interface PivotFetch {}
	public static Set<PivotResponseFieldPair> extractFieldsFromType(Class c)
	{
		var res = new HashSet<PivotResponseFieldPair>();
		var fields = c.getDeclaredFields();
		for (var f : fields) {
			if (f.isAnnotationPresent(PivotFetch.class)) {
				var fieldName = f.getName();
				var fieldType = f.getType().getSimpleName();
				res.add(new PivotResponseFieldPair(fieldName, fieldType));
			}
		}
		return res;
	}

	private Set<PivotResponseFieldPair> fields;
	private Set<PivotResponseFieldPair> fieldDisplay;
	private List<String> fieldOrder;
	private Set<T> records;
}
