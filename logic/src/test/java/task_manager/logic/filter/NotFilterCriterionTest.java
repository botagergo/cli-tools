package task_manager.logic.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.util.Utils;
import task_manager.logic.filter.EqualFilterCriterion;
import task_manager.logic.filter.NotFilterCriterion;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class NotFilterCriterionTest {

    public NotFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertNotEquals(true, false);
        assertNotEquals(false, true);
    }

    void assertNotEquals(boolean operand, boolean expected) throws PropertyException, IOException {
        assertEquals(new NotFilterCriterion(new EqualFilterCriterion("test_boolean", operand))
                .check(propertyOwner, propertyManager), expected);
    }

    @SuppressWarnings("SameParameterValue")
    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

    @Mock
    private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
