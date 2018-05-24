package cucumber.runtime.java;

import cucumber.api.java.ConditionalOnClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilterProcessorTest {

    private FilterProcessor filterProcessor;

    @Before
    public void setUp() throws Exception {
        filterProcessor = new FilterProcessor();
    }

    @Test
    public void filter_includes_class_without_annotation() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(BaseStepDefs.class);

        List<Class<?>> actual = filterProcessor.filter(classes);

        assertEquals(classes, actual);
    }

    @Test
    public void filter_includes_class_with_annotation_with_loaded_class() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(ValidConditionalStepDefs.class);

        List<Class<?>> actual = filterProcessor.filter(classes);

        assertEquals(classes, actual);
    }

    @Test
    public void filter_excludes_class_with_annotation_with_unknown_class() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(InvalidConditionalStepDefs.class);

        List<Class<?>> actual = filterProcessor.filter(classes);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void filter_excludes_class_with_annotation_with_unknown_class_and_valid_class() throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(MultipleConditionalStepDefs.class);

        List<Class<?>> actual = filterProcessor.filter(classes);

        assertTrue(actual.isEmpty());
    }

    public static class BaseStepDefs {
        @cucumber.api.java.Before
        public void m() {
        }
    }

    @ConditionalOnClass("java.lang.String")
    public static class ValidConditionalStepDefs {
        @cucumber.api.java.Before
        public void m() {
        }
    }

    @ConditionalOnClass("not.on.class.path")
    public static class InvalidConditionalStepDefs {
        @cucumber.api.java.Before
        public void m() {
        }
    }

    @ConditionalOnClass({"java.lang.String", "not.on.class.path"})
    public static class MultipleConditionalStepDefs {
        @cucumber.api.java.Before
        public void m() {
        }
    }
}