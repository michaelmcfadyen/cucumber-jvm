package cucumber.runtime.java.filter;

import cucumber.api.java.ConditionalOnClass;

public class ConditionalOnClassGlueCodeClassFilter implements GlueCodeClassFilter {

    @Override
    public boolean isValid(Class<?> glueCodeClass) {
        ConditionalOnClass annotation = glueCodeClass.getAnnotation(ConditionalOnClass.class);
        String[] classNames = annotation.value();
        try {
            for (String className : classNames) {
                Class.forName(className);
            }
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
