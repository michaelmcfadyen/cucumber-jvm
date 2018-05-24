package cucumber.runtime.java.filter;

public interface GlueCodeClassFilter {

    boolean isValid(Class<?> glueCodeClass);
}
