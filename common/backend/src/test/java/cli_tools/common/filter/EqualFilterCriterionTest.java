package cli_tools.common.filter;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.Utils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EqualFilterCriterionTest {

    private final PropertyManager propertyManager;
    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    @Mock
    private PropertyOwner propertyOwner;

    public EqualFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE);
        addPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_string_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        checkEquals("test_string", "string_value");
    }

    @Test
    public void test_boolean_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        Assert.assertTrue(checkEquals("test_boolean", true));
    }

    @Test
    public void test_integer_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 123));
        Assert.assertTrue(checkEquals("test_integer", 123));
    }

    @Test
    public void test_uuid_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        Assert.assertTrue(checkEquals("test_uuid", uuid1));
    }

    @Test
    public void test_list_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value2")));
        Assert.assertTrue(checkEquals("test_string_list", Utils.newArrayList("string_value1", "string_value2")));
    }

    @Test
    public void test_string_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertFalse(checkEquals("test_string", "other_string_value"));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        Assert.assertFalse(checkEquals("test_string", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        Assert.assertFalse(checkEqualsWithDefault("test_string", "other_string_value"));
    }

    @Test
    public void test_boolean_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        Assert.assertFalse(checkEquals("test_boolean", false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        Assert.assertFalse(checkEquals("test_boolean", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        Assert.assertFalse(checkEqualsWithDefault("test_boolean", false));
    }

    @Test
    public void test_integer_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 123));
        Assert.assertFalse(checkEquals("test_integer", 456));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_integer", 123));
        Assert.assertFalse(checkEquals("test_integer", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        Assert.assertFalse(checkEqualsWithDefault("test_integer", 123));
    }

    @Test
    public void test_uuid_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        Assert.assertFalse(checkEquals("test_uuid", uuid2));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        Assert.assertFalse(checkEquals("test_uuid", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        Assert.assertFalse(
                checkEqualsWithDefault("test_uuid", uuid1));
    }

    @Test
    public void test_list_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value3")));
        Assert.assertFalse(
                checkEquals("test_string_list",
                        List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList(null, "string_value3")));
        Assert.assertFalse(
                checkEquals("test_string_list",
                        Utils.newArrayList(null, "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList(null, "string_value3")));
        Assert.assertFalse(
                checkEquals("test_string_list",
                        List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value3")));
        Assert.assertFalse(
                checkEquals("test_string_list", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        Assert.assertFalse(
                checkEqualsWithDefault("test_string_list",
                        List.of("string_value1", "string_value2")));
    }

    private boolean checkEquals(String propertyName, Object operand)
            throws PropertyException, IOException {
        return new EqualFilterCriterion(propertyName, operand)
                .check(propertyOwner, propertyManager);
    }

    private boolean checkEqualsWithDefault(String propertyName, Object operand)
            throws PropertyException, IOException {
        return new EqualFilterCriterion(propertyName, operand)
                .check(propertyOwner, propertyManager);
    }

    private void addPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(propertyName,
                propertyType, null, multiplicity, null, null));
    }

}
