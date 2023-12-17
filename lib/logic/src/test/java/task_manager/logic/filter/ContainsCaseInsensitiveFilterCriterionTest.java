package task_manager.logic.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.util.Utils;
import task_manager.logic.filter.ContainsCaseInsensitiveFilterCriterion;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ContainsCaseInsensitiveFilterCriterionTest {

    public ContainsCaseInsensitiveFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_boolean", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_uuid", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_string_list", PropertyDescriptor.Multiplicity.LIST);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_contains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "VaLuE").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string_value").check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_doesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertFalse(new ContainsCaseInsensitiveFilterCriterion("test_string", "value1").check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String name, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                PropertyDescriptor.Type.String, null, multiplicity, null, false));
    }

    @Mock
    private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
