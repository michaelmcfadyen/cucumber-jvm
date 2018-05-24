package cucumber.runtime.java;

import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static cucumber.runtime.io.MultiLoader.packageName;

class MethodScanner {

    private final ClassFinder classFinder;

    public MethodScanner(ClassFinder classFinder) {
        this.classFinder = classFinder;
    }


    public List<Class<?>> discoverGlueCodeClasses(List<String> gluePaths) {
        List<Class<?>> glueCodeClasses = new ArrayList<Class<?>>();
        for (String gluePath : gluePaths) {
            glueCodeClasses.addAll(classFinder.getDescendants(Object.class, packageName(gluePath)));
        }

        return glueCodeClasses;
    }

    /**
     * Registers step definitions and hooks.
     *
     * @param javaBackend the backend where stepdefs and hooks will be registered
     * @param glueCodeClasses classes to load
     */
    public void scan(JavaBackend javaBackend, List<Class<?>> glueCodeClasses) {
        for (Class<?> glueCodeClass : glueCodeClasses) {
            while (glueCodeClass != null && glueCodeClass != Object.class && !Utils.isInstantiable(glueCodeClass)) {
                // those can't be instantiated without container class present.
                glueCodeClass = glueCodeClass.getSuperclass();
            }
            //prevent unnecessary checking of Object methods
            if (glueCodeClass != null && glueCodeClass != Object.class) {
                for (Method method : glueCodeClass.getMethods()) {
                    if (method.getDeclaringClass() != Object.class) {
                        scan(javaBackend, method, glueCodeClass);
                    }
                }
            }
        }
    }

    /**
     * Registers step definitions and hooks.
     *
     * @param javaBackend   the backend where stepdefs and hooks will be registered.
     * @param method        a candidate for being a stepdef or hook.
     * @param glueCodeClass the class where the method is declared.
     */
    public void scan(JavaBackend javaBackend, Method method, Class<?> glueCodeClass) {
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation annotation : methodAnnotations) {
            if (isHookAnnotation(annotation)) {
                validateMethod(method, glueCodeClass);
                javaBackend.addHook(annotation, method);
            } else if (isStepdefAnnotation(annotation)) {
                validateMethod(method, glueCodeClass);
                javaBackend.addStepDefinition(annotation, method);
            }
        }
    }

    private void validateMethod(Method method, Class<?> glueCodeClass) {
        if (!method.getDeclaringClass().isAssignableFrom(glueCodeClass)) {
            throw new CucumberException(String.format("%s isn't assignable from %s", method.getDeclaringClass(), glueCodeClass));
        }
        if (!glueCodeClass.equals(method.getDeclaringClass())) {
            throw new CucumberException(String.format("You're not allowed to extend classes that define Step Definitions or hooks. %s extends %s", glueCodeClass, method.getDeclaringClass()));
        }
    }

    private boolean isHookAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.equals(Before.class) || annotationClass.equals(After.class) || annotationClass.equals(BeforeStep.class) || annotationClass.equals(AfterStep.class);
    }

    private boolean isStepdefAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.getAnnotation(StepDefAnnotation.class) != null;
    }
}
