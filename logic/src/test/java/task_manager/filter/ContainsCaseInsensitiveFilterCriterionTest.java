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
import task_manager.logic.filter.ContainsCaseInsensitiveFilterCriterion;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ContainsCaseInsensitiveFilterCriterionTest {

    public ContainsCaseInsensitiveFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_string", PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Multiplicity.LIST);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_contains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "VaLuE").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string_value").check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_doesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        assertFalse(new ContainsCaseInsensitiveFilterCriterion("test_string", "value1").check(propertyOwner, propertyManager));
    }

    private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Multiplicity multiplicity) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                PropertyDescriptor.Type.String, null, multiplicity, null));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks
    private PropertyManager propertyManager;

}
