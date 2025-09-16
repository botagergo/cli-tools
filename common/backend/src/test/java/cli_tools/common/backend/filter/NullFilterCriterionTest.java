package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.util.Utils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class NullFilterCriterionTest {

    private final PropertyManager propertyManager;
    @Mock
    private PropertyOwner propertyOwner;

    public NullFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_string_list", PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_string_set", PropertyDescriptor.Multiplicity.SET);
    }

    @BeforeMethod
    void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    void test_null() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string", null,
                "test_string_set", null
        ));
        assertNull("test_string");
        assertNull("test_string_set");
    }

    @Test
    void test_notNull() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string", "str1",
                "test_string_set", Utils.newLinkedHashSet("abcde"),
                "test_string_list", Utils.newArrayList()
        ));
        assertNotNull("test_string");
        assertNotNull("test_string_set");
        assertNotNull("test_string_list");
    }

    private void assertNull(String propertyName)
            throws PropertyException, IOException {
        assertTrue(new NullFilterCriterion(propertyName).check(propertyOwner, propertyManager));
    }

    private void assertNotNull(String propertyName)
            throws PropertyException, IOException {
        assertFalse(new NullFilterCriterion(propertyName).check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                PropertyDescriptor.Type.String, null, multiplicity, null, null));
    }

}
