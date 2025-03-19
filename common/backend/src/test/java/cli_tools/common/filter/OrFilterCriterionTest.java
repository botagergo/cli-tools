package cli_tools.common.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.util.Utils;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

import java.io.IOException;

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
        Assert.assertTrue(checkEqual(true, false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", false, "test_boolean2", false));
        Assert.assertTrue(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        Assert.assertTrue(checkEqual(false, true));
    }

    @Test
    public void test_check_not_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", false));
        Assert.assertFalse(checkEqual(false, true));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean1", true, "test_boolean2", true));
        Assert.assertFalse(checkEqual(false, false));
    }

    boolean checkEqual(boolean operand1, boolean operand2) throws PropertyException, IOException {
        return new OrFilterCriterion(
                new EqualFilterCriterion("test_boolean1", operand1),
                new EqualFilterCriterion("test_boolean2", operand2))
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String name) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
    }

    @Mock private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
