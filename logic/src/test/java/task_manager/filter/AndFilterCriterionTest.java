package task_manager.filter;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertyOwner;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.core.util.Utils;
import task_manager.logic.filter.AndFilterCriterion;
import task_manager.logic.filter.EqualFilterCriterion;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AndFilterCriterionTest {

    public AndFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_boolean1");
        mockitoPropertyDescriptor("test_boolean2");
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", false));
        assertTrue(checkEqual(true, false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", false, "test_boolean2", true));
        assertTrue(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", false, "test_boolean2", false));
        assertTrue(checkEqual(false, false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        assertTrue(checkEqual(true, true));
    }

    @Test
    public void test_check_not_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", false, "test_boolean2", false));
        assertFalse(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        assertFalse(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        assertFalse(checkEqual(false, false));
    }

    boolean checkEqual(boolean operand1, boolean operand2) throws PropertyException, IOException {
        return new AndFilterCriterion(
                new EqualFilterCriterion("test_boolean1", operand1),
                new EqualFilterCriterion("test_boolean2", operand2))
                .check(propertyOwner, propertyManager);
    }

    private void mockitoPropertyDescriptor(String name) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks private PropertyManager propertyManager;

}
