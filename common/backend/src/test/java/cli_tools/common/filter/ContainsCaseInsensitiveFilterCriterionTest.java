package cli_tools.common.filter;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.util.Utils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

public class ContainsCaseInsensitiveFilterCriterionTest {

    private final PropertyManager propertyManager;
    @Mock
    private PropertyOwner propertyOwner;

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
    public void test_contains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "VaLuE").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "").check(propertyOwner, propertyManager));
        Assert.assertTrue(new ContainsCaseInsensitiveFilterCriterion("test_string", "string_value").check(propertyOwner, propertyManager));
    }

    @Test
    public void test_doesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertFalse(new ContainsCaseInsensitiveFilterCriterion("test_string", "value1").check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String name, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                PropertyDescriptor.Type.String, null, multiplicity, null, null));
    }

}
