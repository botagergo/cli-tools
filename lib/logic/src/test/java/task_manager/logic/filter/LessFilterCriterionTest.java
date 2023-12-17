package task_manager.logic.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.logic.PropertyComparator;
import task_manager.property_lib.*;
import task_manager.util.Utils;

import java.io.IOException;

public class LessFilterCriterionTest {

    public LessFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string", PropertyDescriptor.Type.String);
        addPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean);
        addPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer);
        addPropertyDescriptor("test_integer1", PropertyDescriptor.Type.Integer);
        addPropertyDescriptor("test_integer2", PropertyDescriptor.Type.Integer);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_less_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertLessNullsFirst("test_string", "str2");
    }

    @Test
    public void test_check_notLess_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertNotLessNullsFirst("test_string", "str1");
        assertNotLessNullsFirst("test_string", "str0");
    }

    @Test
    public void test_check_less_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", false));
        assertLessNullsFirst("test_boolean", true);
    }

    @Test
    public void test_check_notLess_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertNotLessNullsFirst("test_boolean", true);
        assertNotLessNullsFirst("test_boolean", false);
    }

    @Test
    public void test_check_less_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertLessNullsFirst("test_integer", 10);
    }

    @Test
    public void test_check_notLess_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertNotLessNullsFirst("test_integer", 2);
        assertNotLessNullsFirst("test_integer", 4);
    }

    @Test
    public void test_check_less_withNulls() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer1", 10, "test_integer2", null));
        assertNotLessNullsFirst("test_integer1", null);
        assertLessNullsFirst("test_integer2", 4);
        assertNotLessNullsFirst("test_integer2", null);
        assertLessNullsLast("test_integer1", null);
        assertNotLessNullsLast("test_integer2", 4);
        assertNotLessNullsLast("test_integer2", null);
    }

    private void assertLessNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsFirst(propertyName, operand));
    }

    private void assertNotLessNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsFirst(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertLessNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsLast(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotLessNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsLast(propertyName, operand));
    }

    private boolean checkNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsFirstPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsLastPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

    private final PropertyComparator nullsFirstPropertyComparator = new PropertyComparator(true);
    private final PropertyComparator nullsLastPropertyComparator = new PropertyComparator(false);
    @Mock
    private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
