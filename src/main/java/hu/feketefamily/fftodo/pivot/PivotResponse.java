package hu.feketefamily.fftodo.pivot;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class PivotResponse<T> {

	public static Map<String, String> ExtractFieldsFromType(Class c)
	{
		var res = new HashMap<String, String>();
		var fields = c.getFields();
		for (var f : fields)
			res.put(f.getName(), f.getClass().getName());
		return res;
	}

	private Map<String, String> fields;
	private List<String> fieldOrder;
	private Set<T> records;
}
