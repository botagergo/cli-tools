package task_manager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.*;
import task_manager.filter.ContainsCaseInsensitiveFilterCriterion;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;
import task_manager.repository.PropertyDescriptorRepository;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>(Map.of("test_string", "string_value")));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "VaLuE").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "").check(propertyOwner, propertyManager));
        assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string_value").check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_doesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>(Map.of("test_string", "string_value")));
        assertFalse(new ContainsCaseInsensitiveFilterCriterion("test_string", "value1").check(propertyOwner, propertyManager));
    }

    private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Multiplicity multiplicity) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                PropertyDescriptor.Type.String, multiplicity, null));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks
    private PropertyManager propertyManager;

}
