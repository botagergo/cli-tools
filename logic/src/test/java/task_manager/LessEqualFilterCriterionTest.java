package task_manager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.core.property.*;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.core.util.Utils;
import task_manager.logic.PropertyComparator;
import task_manager.logic.filter.LessEqualFilterCriterion;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LessEqualFilterCriterionTest {

    private final PropertyComparator nullsFirstPropertyComparator = new PropertyComparator(true);
    private final PropertyComparator nullsLastPropertyComparator = new PropertyComparator(false);
    @Mock
    private PropertyOwner propertyOwner;
    @Mock
    private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks
    private PropertyManager propertyManager;

    public LessEqualFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean);
        mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer);
        mockitoPropertyDescriptor("test_integer1", PropertyDescriptor.Type.Integer);
        mockitoPropertyDescriptor("test_integer2", PropertyDescriptor.Type.Integer);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_lessEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertLessEqualNullsFirst("test_string", "str1");
        assertLessEqualNullsFirst("test_string", "str2");
    }

    @Test
    public void test_check_notLessEqual_string() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "str1"));
        assertNotLessEqualNullsFirst("test_string", "str0");
    }

    @Test
    public void test_check_lessEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", false));
        assertLessEqualNullsFirst("test_boolean", true);
        assertLessEqualNullsFirst("test_boolean", false);
    }

    @Test
    public void test_check_notLessEqual_boolean() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertNotLessEqualNullsFirst("test_boolean", false);
    }

    @Test
    public void test_check_lessEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 4));
        assertLessEqualNullsFirst("test_integer", 10);
        assertLessEqualNullsFirst("test_integer", 4);
    }

    @Test
    public void test_check_notLessEqual_integer() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 10));
        assertNotLessEqualNullsFirst("test_integer", 2);
    }

    @Test
    public void test_check_lessEqual_withNulls() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer1", 10, "test_integer2", null));
        assertNotLessEqualNullsFirst("test_integer1", null);
        assertLessEqualNullsFirst("test_integer2", 4);
        assertLessEqualNullsFirst("test_integer2", null);
        assertLessEqualNullsLast("test_integer1", null);
        assertNotLessEqualNullsLast("test_integer2", 4);
        assertLessEqualNullsLast("test_integer2", null);
    }

    private void assertLessEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        assertTrue(checkNullsFirst(propertyName, operand));
    }

    private void assertNotLessEqualNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        assertFalse(checkNullsFirst(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertLessEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        assertTrue(checkNullsLast(propertyName, operand));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertNotLessEqualNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        assertFalse(checkNullsLast(propertyName, operand));
    }

    private boolean checkNullsFirst(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyDescriptorRepository.get(propertyName), operand), nullsFirstPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkNullsLast(String propertyName, Object operand) throws IOException, PropertyException {
        return new LessEqualFilterCriterion(propertyName, Property.fromUnchecked(propertyDescriptorRepository.get(propertyName), operand), nullsLastPropertyComparator)
                .check(propertyOwner, propertyManager);
    }

    private void mockitoPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(propertyName)).thenReturn(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null));
    }

}
