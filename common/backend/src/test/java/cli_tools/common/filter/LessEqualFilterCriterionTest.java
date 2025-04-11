package cli_tools.common.filter;

import cli_tools.common.property_comparator.PropertyComparator;
import cli_tools.common.property_lib.*;
import cli_tools.common.util.Utils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

public class LessEqualFilterCriterionTest {

    private final PropertyComparator nullsFirstPropertyComparator = new PropertyComparator(true);
    private final PropertyComparator nullsLastPropertyComparator = new PropertyComparator(false);
    private final PropertyManager propertyManager;
    @Mock
    private PropertyOwner propertyOwner;

    public LessEqualFilterCriterionTest() {
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
    public void test_lessEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertLessEqualNullsFirst("test_string", "str1");
        assertLessEqualNullsFirst("test_string", "str2");
    }

    @Test
    public void test_notLessEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertNotLessEqualNullsFirst("test_string", "str0");
    }

    @Test
    public void test_lessEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", false));
        assertLessEqualNullsFirst("test_boolean", true);
        assertLessEqualNullsFirst("test_boolean", false);
    }

    @Test
    public void test_notLessEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertNotLessEqualNullsFirst("test_boolean", false);
    }

    @Test
    public void test_lessEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertLessEqualNullsFirst("test_integer", 10);
        assertLessEqualNullsFirst("test_integer", 4);
    }

    @Test
    public void test_notLessEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 10));
        assertNotLessEqualNullsFirst("test_integer", 2);
    }

    @Test
    public void test_lessEqual_withNulls() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer1", 10, "test_integer2", null));
        assertNotLessEqualNullsFirst("test_integer1", null);
        assertLessEqualNullsFirst("test_integer2", 4);
        assertLessEqualNullsFirst("test_integer2", null);
        assertLessEqualNullsLast("test_integer1", null);
        assertNotLessEqualNullsLast("test_integer2", 4);
        assertLessEqualNullsLast("test_integer2", null);
    }

    private void assertLessEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsFirst(propertyName, operand));
    }

    private void assertNotLessEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsFirst(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertLessEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsLast(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotLessEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsLast(propertyName, operand));
    }

    private boolean checkNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsFirstPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsLastPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
    }

}
