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
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.Utils;
import task_manager.logic.filter.EqualFilterCriterion;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EqualFilterCriterionTest {

    public EqualFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_string_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        checkEquals("test_string", "string_value");
    }

    @Test
    public void test_check_boolean_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertTrue(checkEquals("test_boolean", true));
    }

    @Test
    public void test_check_uuid_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        assertTrue(checkEquals("test_uuid", uuid1));
    }

    @Test
    public void test_check_list_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value2")));
        assertTrue(checkEquals("test_string_list", Utils.newArrayList("string_value1", "string_value2")));
    }

    @Test
    public void test_check_string_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        assertFalse(checkEquals("test_string", "other_string_value"));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string", "string_value"));
        assertFalse(checkEquals("test_string", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        assertFalse(checkEqualsWithDefault("test_string", "other_string_value"));
    }

    @Test
    public void test_check_boolean_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertFalse(checkEquals("test_boolean", false));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_boolean", true));
        assertFalse(checkEquals("test_boolean", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        assertFalse(checkEqualsWithDefault("test_boolean", false));
    }

    @Test
    public void test_check_uuid_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        assertFalse(checkEquals("test_uuid", uuid2));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_uuid", uuid1));
        assertFalse(checkEquals("test_uuid", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        assertFalse(
            checkEqualsWithDefault("test_uuid", uuid1));
    }

    @Test
    public void test_check_list_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value3")));
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList(null, "string_value3")));
        assertFalse(
            checkEquals("test_string_list",
                Utils.newArrayList(null, "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList(null, "string_value3")));
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("string_value1", "string_value3")));
        assertFalse(
            checkEquals("test_string_list", null));

        Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>());
        assertFalse(
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

    private void mockitoPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(propertyName)).thenReturn(new PropertyDescriptor(propertyName,
                propertyType, null, multiplicity, null, false));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks private PropertyManager propertyManager;
    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

}
