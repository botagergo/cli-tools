package task_manager.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.util.Utils;
import task_manager.logic.filter.EqualFilterCriterion;
import task_manager.logic.filter.OrFilterCriterion;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class OrFilterCriterionTest {

    public OrFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_boolean1");
        addPropertyDescriptor("test_boolean2");
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", false));
        assertTrue(checkEqual(true, false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", false, "test_boolean2", false));
        assertTrue(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        assertTrue(checkEqual(false, true));
    }

    @Test
    public void test_check_not_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", false));
        assertFalse(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        assertFalse(checkEqual(false, false));
    }

    boolean checkEqual(boolean operand1, boolean operand2) throws PropertyException, IOException {
        return new OrFilterCriterion(
                new EqualFilterCriterion("test_boolean1", operand1),
                new EqualFilterCriterion("test_boolean2", operand2))
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String name) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

    @Mock private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
