package hu.feketefamily.fftodo.pivot;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Data
@NoArgsConstructor
public class PivotResponse<T> {

	public static Map<String, String> extractFieldsFromType(Class c)
	{
		var res = new HashMap<String, String>();
		var fields = c.getFields();
		for (var f : fields) {
			var fieldName = f.getName();
			var fieldType= f.getType().getName();
			res.put(fieldName, fieldType);
		}
		return res;
	}

	private Map<String, String> fields;
	private List<String> fieldOrder;
	private Set<T> records;
}
