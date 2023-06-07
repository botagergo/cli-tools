package task_manager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.*;
import task_manager.filter.AndFilterCriterion;
import task_manager.filter.EqualsFilterCriterion;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;
import task_manager.repository.PropertyDescriptorRepository;
import task_manager.util.Utils;

import static org.testng.Assert.*;

import java.io.IOException;

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
                new EqualsFilterCriterion("test_boolean1", operand1),
                new EqualsFilterCriterion("test_boolean2", operand2))
                .check(propertyOwner, propertyManager);
    }

    private void mockitoPropertyDescriptor(String name) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks private PropertyManager propertyManager;

}
