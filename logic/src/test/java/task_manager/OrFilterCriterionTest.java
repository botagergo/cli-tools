package task_manager;

import org.testng.annotations.*;
import task_manager.filter.EqualsFilterCriterion;
import task_manager.filter.OrFilterCriterion;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyManager;
import task_manager.data.property.PropertyOwner;
import static org.testng.Assert.*;
import java.util.List;

public class OrFilterCriterionTest {

    @Test
    public void testTrue() throws PropertyException {
        assertTrue(checkEqual(true, true, true, false));
        assertTrue(checkEqual(false, true, true, true));
        assertTrue(checkEqual(false, false, true, true));
    }

    @Test
    public void testFalse() throws PropertyException {
        assertFalse(checkEqual(false, true, true, false));
    }

    boolean checkEqual(boolean operand1, boolean propertyValue1, boolean operand2,
        boolean propertyValue2) throws PropertyException {
        return new OrFilterCriterion(
            new EqualsFilterCriterion("test_boolean1", operand1),
            new EqualsFilterCriterion("test_boolean2", operand2)).check(
                getPropertyOwner()
                    .setProperty("test_boolean1", propertyValue1)
                    .setProperty("test_boolean2", propertyValue2));
    }

    PropertyOwner getPropertyOwner() {
        return new PropertyOwnerImpl(propertyManager);
    }

    final PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection(List.of(
        new PropertyDescriptor("test_boolean1", PropertyDescriptor.Type.Boolean, false, null),
        new PropertyDescriptor("test_boolean2", PropertyDescriptor.Type.Boolean, false, null)));

    final PropertyManager propertyManager = new PropertyManager(propertyDescriptors);
    PropertyOwner propertyOwner = new PropertyOwnerImpl(propertyManager);
}
