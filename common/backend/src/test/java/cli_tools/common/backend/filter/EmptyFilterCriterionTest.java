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

public class EmptyFilterCriterionTest {

    private final PropertyManager propertyManager;
    @Mock
    private PropertyOwner propertyOwner;

    public EmptyFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string1", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_string2", PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_string_list1", PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_string_list2", PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_string_set1", PropertyDescriptor.Multiplicity.SET);
        addPropertyDescriptor("test_string_set2", PropertyDescriptor.Multiplicity.SET);
    }

    @BeforeMethod
    void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    void test_empty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string1", null,
                "test_string2", ""
        ));
        assertEmpty("test_string1");
        assertEmpty("test_string2");
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list1", null,
                "test_string_list2", Utils.newArrayList(),
                "test_string_set1", null,
                "test_string_set2", Utils.newLinkedHashSet()
        ));
        assertEmpty("test_string_list1");
        assertEmpty("test_string_list2");
        assertEmpty("test_string_set1");
        assertEmpty("test_string_set2");
    }

    @Test
    void test_notEmpty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string1", "str1",
                "test_string2", " "
        ));
        assertNotEmpty("test_string1");
        assertNotEmpty("test_string2");
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list1", Utils.newArrayList("str1"),
                "test_string_list2", Utils.newArrayList(""),
                "test_string_set1", Utils.newLinkedHashSet("str1"),
                "test_string_set2", Utils.newLinkedHashSet("")
        ));
        assertNotEmpty("test_string_list1");
        assertNotEmpty("test_string_list2");
        assertNotEmpty("test_string_set1");
        assertNotEmpty("test_string_set2");
    }

    private void assertEmpty(String propertyName)
            throws PropertyException, IOException {
        assertTrue(new EmptyFilterCriterion(propertyName).check(propertyOwner, propertyManager));
    }

    private void assertNotEmpty(String propertyName)
            throws PropertyException, IOException {
        assertFalse(new EmptyFilterCriterion(propertyName).check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                PropertyDescriptor.Type.String, null, multiplicity, null, null));
    }

}
