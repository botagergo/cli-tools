package task_manager;

import org.testng.annotations.*;
import task_manager.filter.ContainsCaseInsensitiveFilterCriterion;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyManager;
import task_manager.data.property.PropertyOwner;
import static org.testng.Assert.*;
import java.util.List;

public class ContainsCaseInsensitiveFilterCriterionTest {

    @Test
    public void testContains() throws PropertyException {
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string")
            .check(getPropertyOwner().setProperty("test_string", "string_value")));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "VaLuE")
            .check(getPropertyOwner().setProperty("test_string", "string_value")));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "")
            .check(getPropertyOwner().setProperty("test_string", "string_value")));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string_value")
            .check(getPropertyOwner().setProperty("test_string", "string_value")));
    }

    @Test
    public void testNotContains() throws PropertyException {
        assertFalse(new ContainsCaseInsensitiveFilterCriterion("test_string", "value1")
            .check(getPropertyOwner().setProperty("test_string", "string_value")));
    }

    private PropertyOwner getPropertyOwner() {
        return new PropertyOwnerImpl(propertyManager);
    }

    final PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection(List.of(
        new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, null),
        new PropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false,
            null),
        new PropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, null),
        new PropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true,
            null)));


    final PropertyManager propertyManager = new PropertyManager(propertyDescriptors);
}
