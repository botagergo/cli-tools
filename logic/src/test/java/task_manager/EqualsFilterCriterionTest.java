package task_manager;

import org.testng.annotations.*;

import task_manager.filter.EqualsFilterCriterion;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyManager;
import task_manager.data.property.PropertyOwner;
import static org.testng.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EqualsFilterCriterionTest {

    @Test
    public void testStringEquals() throws PropertyException {
        checkEquals("test_string", "string_value", "string_value");
    }

    @Test
    public void testBooleanEquals() throws PropertyException {
        assertTrue(checkEquals("test_boolean", true, true));
    }

    @Test
    public void testUuidEquals() throws PropertyException {
        assertTrue(checkEquals("test_uuid",
            UUID.fromString("c7817f46-77c4-48a7-93d2-41016a2a2682"),
            UUID.fromString("c7817f46-77c4-48a7-93d2-41016a2a2682")));
    }

    @Test
    public void testListEquals() throws PropertyException {
        assertTrue(checkEquals("test_string_list",
            List.of("string_value1", "string_value2"), List.of("string_value1", "string_value2")));
    }

    @Test
    public void testStringNotEquals() throws PropertyException {
        assertFalse(checkEquals("test_string", "other_string_value", "string_value"));
        assertFalse(checkEquals("test_string", null, "string_value"));
        assertFalse(checkEqualsWithDefault("test_string", "other_string_value"));
    }

    @Test
    public void testBooleanNotEquals() throws PropertyException {
        assertFalse(checkEquals("test_boolean", false, true));
        assertFalse(checkEquals("test_boolean", null, true));
        assertFalse(checkEqualsWithDefault("test_boolean", false));
    }

    @Test
    public void testUuidNotEquals() throws PropertyException {
        assertFalse(
            checkEquals("test_uuid",
                UUID.fromString("a7817f46-77c4-48a7-93d2-41016a2a2682"),
                UUID.fromString("c7817f46-77c4-48a7-93d2-41016a2a2682")));
        assertFalse(
            checkEquals("test_uuid", null,
                UUID.fromString("c7817f46-77c4-48a7-93d2-41016a2a2682")));
        assertFalse(
            checkEqualsWithDefault("test_uuid",
                UUID.fromString("a7817f46-77c4-48a7-93d2-41016a2a2682")));
    }

    @Test
    public void testListNotEquals() throws PropertyException {
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2"),
                List.of("string_value1", "string_value3")));
        assertFalse(
            checkEquals("test_string_list",
                listOfFuckingNull(null, "string_value2"),
                listOfFuckingNull(null, "string_value3")));
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2"),
                listOfFuckingNull(null, "string_value3")));
        assertFalse(
            checkEquals("test_string_list", null, List.of("string_value1", "string_value3")));
        assertFalse(
            checkEqualsWithDefault("test_string_list",
                List.of("string_value1", "string_value2")));
    }

    boolean checkEquals(String propertyName, Object operand, Object propertyValue)
        throws PropertyException {
        return new EqualsFilterCriterion(propertyName, operand)
            .check(getPropertyOwner().setProperty(propertyName, propertyValue));
    }

    boolean checkEqualsWithDefault(String propertyName, Object operand)
        throws PropertyException {
        return new EqualsFilterCriterion(propertyName, operand)
            .check(getPropertyOwner());
    }

    PropertyOwner getPropertyOwner() {
        return new PropertyOwnerImpl(propertyManager);
    }

    // Fucking java doesn't allow fucking nulls in fucking List.of...
    @SafeVarargs
    final <T> List<T> listOfFuckingNull(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    final PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection(List.of(
        new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, null),
        new PropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, null),
        new PropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, null),
        new PropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true,
            null)));


    final PropertyManager propertyManager = new PropertyManager(propertyDescriptors);
}
