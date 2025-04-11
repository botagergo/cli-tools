package cli_tools.common.filter;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.util.Utils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class InFilterCriterionTest {

    @Mock
    private PropertyOwner propertyOwner;
    @InjectMocks
    private PropertyManager propertyManager;

    public InFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
    }

    @BeforeMethod
    void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    void test_in() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string", "str1"
        ));
        Assert.assertTrue(new InFilterCriterion("test_string", List.of("str", "str1", "str2")).check(propertyOwner, propertyManager));
    }

    @Test
    void test_not_in() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string", "str1"
        ));
        Assert.assertFalse(new InFilterCriterion("test_string", List.of("str2", "str3", "str4")).check(propertyOwner, propertyManager));
        Assert.assertFalse(new InFilterCriterion("test_string", List.of()).check(propertyOwner, propertyManager));
        Assert.assertFalse(new InFilterCriterion("test_string", null).check(propertyOwner, propertyManager));
    }
}
