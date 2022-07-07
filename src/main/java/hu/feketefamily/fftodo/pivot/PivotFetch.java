package hu.feketefamily.fftodo.pivot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PivotFetch {
	int order() default 0;
	String role() default "";
	String display() default "";
}
