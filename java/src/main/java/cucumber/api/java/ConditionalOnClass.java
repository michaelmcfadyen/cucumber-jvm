package cucumber.api.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionalOnClass {

    /**
     * @return class name including package name that must be present for this class to be included as glue code
     */
    String[] value() default {};

}
