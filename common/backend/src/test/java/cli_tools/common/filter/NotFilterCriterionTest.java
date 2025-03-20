package cli_tools.common.filter;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.util.Utils;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

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
    public void test_equals() throws PropertyException, IOException {
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
                propertyType, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
    }

    @Mock
    private PropertyOwner propertyOwner;
    private final PropertyManager propertyManager;

}
