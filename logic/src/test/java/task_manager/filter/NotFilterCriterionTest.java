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
import task_manager.logic.filter.EqualFilterCriterion;
import task_manager.logic.filter.NotFilterCriterion;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class NotFilterCriterionTest {

    @Mock
    private PropertyDescriptorRepository propertyDescriptorRepository;

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
    @InjectMocks
    private PropertyManager propertyManager;

    @Mock
    private PropertyOwner propertyOwner;
    public NotFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean);
    }

    @SuppressWarnings("SameParameterValue")
    private void mockitoPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(propertyName)).thenReturn(new PropertyDescriptor(propertyName,
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

}
