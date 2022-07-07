package hu.feketefamily.fftodo.pivot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PivotResponse<T> {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface PivotFetch {
		int order() default 0;
		String role() default "";
		String display() default "";
	}
	public static Set<PivotResponseFieldPair> extractFieldsFromType(Class c)
	{
		var res = new HashSet<PivotResponseFieldPair>();
		var fields = c.getDeclaredFields();
		for (var f : fields) {
			if (f.isAnnotationPresent(PivotFetch.class)) {
				var fieldName = f.getName();
				var fieldType = f.getType().getSimpleName();
				var fieldAnno = f.getAnnotation(PivotFetch.class);
				if (!fieldAnno.role().equals(""))
					fieldType += "," + fieldAnno.role();
				res.add(new PivotResponseFieldPair(fieldName, fieldType));
			}
		}
		return res;
	}

	public static List<String> extractFieldOrderFromType(Class c)
	{
		return Arrays.stream(c.getDeclaredFields()).sorted(new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				int ord1=0, ord2=0;
				if (o1.isAnnotationPresent(PivotFetch.class))
					ord1 = o1.getAnnotation(PivotFetch.class).order();
				if (o2.isAnnotationPresent(PivotFetch.class))
					ord2 = o2.getAnnotation(PivotFetch.class).order();
				return ord1 - ord2;
			}
		}).map(f -> f.getName()).collect(Collectors.toList());
	}

	public static Set<PivotResponseFieldPair> extractFieldDisplayFromType(Class c)
	{
		var res = new HashSet<PivotResponseFieldPair>();
		var fields = c.getDeclaredFields();
		for (var f : fields) {
			if (f.isAnnotationPresent(PivotFetch.class)) {
				var fieldName = f.getName();
				var fieldDisplay = f.getAnnotation(PivotFetch.class).display();
				if (!fieldDisplay.equals(""))
					res.add(new PivotResponseFieldPair(fieldName, fieldDisplay));
			}
		}
		return res;
	}

	private Set<PivotResponseFieldPair> fields;
	private Set<PivotResponseFieldPair> fieldDisplay;
	private List<String> fieldOrder;
	private Set<T> records;
}
