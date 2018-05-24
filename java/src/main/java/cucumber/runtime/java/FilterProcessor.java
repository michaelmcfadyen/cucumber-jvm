package cucumber.runtime.java;

import cucumber.api.java.ConditionalOnClass;
import cucumber.runtime.java.filter.ConditionalOnClassGlueCodeClassFilter;
import cucumber.runtime.java.filter.GlueCodeClassFilter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterProcessor {
    private final Map<Class<? extends Annotation>, GlueCodeClassFilter> glueCodeClassFilters;

    public FilterProcessor() {
        glueCodeClassFilters = new HashMap<Class<? extends Annotation>, GlueCodeClassFilter>();
        glueCodeClassFilters.put(ConditionalOnClass.class, new ConditionalOnClassGlueCodeClassFilter());
    }

    public List<Class<?>> filter(List<Class<?>> glueCodeClasses) {
        ArrayList<Class<?>> validClasses = new ArrayList<Class<?>>();
        for (Class<?> glueCodeClass : glueCodeClasses) {
            validClasses.add(glueCodeClass);

            for(Annotation annotation : glueCodeClass.getAnnotations()) {
                GlueCodeClassFilter glueCodeClassFilter = glueCodeClassFilters.get(annotation.annotationType());
                if(glueCodeClassFilter != null) {
                    if(!glueCodeClassFilter.isValid(glueCodeClass)) {
                        validClasses.remove(glueCodeClass);
                    }
                }
            }
        }
        return validClasses;
    }
}
