package cli_tools.common.filter;

import cli_tools.common.property_lib.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.property_comparator.PropertyComparator;
import cli_tools.common.util.Utils;

import java.io.IOException;

public class GreaterFilterCriterionTest {

    public GreaterFilterCriterionTest() {
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
    public void test_greater_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str2"));
        assertGreaterNullsFirst("test_string", "str1");
    }

    @Test
    public void test_notGreater_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertNotGreaterNullsFirst("test_string", "str1");
        assertNotGreaterNullsFirst("test_string", "str2");
    }

    @Test
    public void test_greater_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertGreaterNullsFirst("test_boolean", false);
    }

    @Test
    public void test_notGreater_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", false));
        assertNotGreaterNullsFirst("test_boolean", true);
        assertNotGreaterNullsFirst("test_boolean", false);
    }

    @Test
    public void test_greater_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertGreaterNullsFirst("test_integer", 2);
    }

    @Test
    public void test_notGreater_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertNotGreaterNullsFirst("test_integer", 10);
        assertNotGreaterNullsFirst("test_integer", 4);
    }

    @Test
    public void test_withNulls() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer1", 10, "test_integer2", null));
        assertGreaterNullsFirst("test_integer1", null);
        assertNotGreaterNullsFirst("test_integer2", 4);
        assertNotGreaterNullsFirst("test_integer2", null);
        assertNotGreaterNullsLast("test_integer1", null);
        assertGreaterNullsLast("test_integer2", 4);
        assertNotGreaterNullsLast("test_integer2", null);
    }

    private void assertGreaterNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsFirst(propertyName, operand));
    }

    private void assertNotGreaterNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsFirst(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertGreaterNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertTrue(checkNullsLast(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotGreaterNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        Assert.assertFalse(checkNullsLast(propertyName, operand));
    }

    private boolean checkNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        return new GreaterFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsFirstPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        return new GreaterFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsLastPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
    }

    private final PropertyComparator nullsFirstPropertyComparator = new PropertyComparator(true);
    private final PropertyComparator nullsLastPropertyComparator = new PropertyComparator(false);
    @Mock
    private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;


}
