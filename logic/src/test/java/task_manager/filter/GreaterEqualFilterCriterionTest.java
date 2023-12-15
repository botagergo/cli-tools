package task_manager.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.util.Utils;
import task_manager.logic.PropertyComparator;
import task_manager.logic.filter.GreaterEqualFilterCriterion;
import task_manager.property_lib.*;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class GreaterEqualFilterCriterionTest {

    public GreaterEqualFilterCriterionTest() {
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
    public void test_check_greaterEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str2"));
        assertGreaterEqualNullsFirst("test_string", "str1");
        assertGreaterEqualNullsFirst("test_string", "str2");
    }

    @Test
    public void test_check_notGreaterEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertNotGreaterEqualNullsFirst("test_string", "str2");
    }

    @Test
    public void test_check_greaterEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertGreaterEqualNullsFirst("test_boolean", true);
        assertGreaterEqualNullsFirst("test_boolean", false);
    }

    @Test
    public void test_check_notGreaterEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", false));
        assertNotGreaterEqualNullsFirst("test_boolean", true);
    }

    @Test
    public void test_check_greaterEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertGreaterEqualNullsFirst("test_integer", 2);
        assertGreaterEqualNullsFirst("test_integer", 4);
    }

    @Test
    public void test_check_notGreaterEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 2));
        assertNotGreaterEqualNullsFirst("test_integer", 10);
    }

    @Test
    public void test_check_greaterEqual_withNulls() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer1", 10, "test_integer2", null));
        assertGreaterEqualNullsFirst("test_integer1", null);
        assertNotGreaterEqualNullsFirst("test_integer2", 4);
        assertGreaterEqualNullsFirst("test_integer2", null);
        assertNotGreaterEqualNullsLast("test_integer1", null);
        assertGreaterEqualNullsLast("test_integer2", 4);
        assertGreaterEqualNullsLast("test_integer2", null);
    }

    private void assertGreaterEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        assertTrue(checkNullsFirst(propertyName, operand));
    }

    private void assertNotGreaterEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        assertFalse(checkNullsFirst(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertGreaterEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        assertTrue(checkNullsLast(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotGreaterEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        assertFalse(checkNullsLast(propertyName, operand));
    }

    private boolean checkNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        return new GreaterEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsFirstPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        return new GreaterEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyManager.getPropertyDescriptorCollection().get(propertyName), operand), nullsLastPropertyComparator)
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
