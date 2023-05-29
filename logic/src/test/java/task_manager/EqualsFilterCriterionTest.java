package task_manager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.*;

import task_manager.filter.EqualsFilterCriterion;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;
import task_manager.repository.PropertyDescriptorRepository;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.*;

public class EqualsFilterCriterionTest {

    public EqualsFilterCriterionTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false);
        mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false);
        mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_string_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string", "string_value")));
        checkEquals("test_string", "string_value");
    }

    @Test
    public void test_check_boolean_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_boolean", true)));
        assertTrue(checkEquals("test_boolean", true));
    }

    @Test
    public void test_check_uuid_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_uuid", "c7817f46-77c4-48a7-93d2-41016a2a2682")));
        assertTrue(checkEquals("test_uuid", "c7817f46-77c4-48a7-93d2-41016a2a2682"));
    }

    @Test
    public void test_check_list_equals() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string_list", List.of("string_value1", "string_value2"))));
        assertTrue(checkEquals("test_string_list", List.of("string_value1", "string_value2")));
    }

    @Test
    public void test_check_string_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string", "string_value")));
        assertFalse(checkEquals("test_string", "other_string_value"));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string", "string_value")));
        assertFalse(checkEquals("test_string", null));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>());
        assertFalse(checkEqualsWithDefault("test_string", "other_string_value"));
    }

    @Test
    public void test_check_boolean_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_boolean", true)));
        assertFalse(checkEquals("test_boolean", false));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_boolean", true)));
        assertFalse(checkEquals("test_boolean", null));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>());
        assertFalse(checkEqualsWithDefault("test_boolean", false));
    }

    @Test
    public void test_check_uuid_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_uuid", "c7817f46-77c4-48a7-93d2-41016a2a2682")));
        assertFalse(
            checkEquals("test_uuid",
                UUID.fromString("a7817f46-77c4-48a7-93d2-41016a2a2682")));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_uuid", "c7817f46-77c4-48a7-93d2-41016a2a2682")));
        assertFalse(
            checkEquals("test_uuid", null));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>());
        assertFalse(
            checkEqualsWithDefault("test_uuid",
                UUID.fromString("a7817f46-77c4-48a7-93d2-41016a2a2682")));
    }

    @Test
    public void test_check_list_not_equal() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string_list", List.of("string_value1", "string_value3"))));
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string_list", listOfFuckingNull(null, "string_value3"))));
        assertFalse(
            checkEquals("test_string_list",
                listOfFuckingNull(null, "string_value2")));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string_list", listOfFuckingNull(null, "string_value3"))));
        assertFalse(
            checkEquals("test_string_list",
                List.of("string_value1", "string_value2")));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of("test_string_list", List.of("string_value1", "string_value3"))));
        assertFalse(
            checkEquals("test_string_list", null));

        Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>());
        assertFalse(
            checkEqualsWithDefault("test_string_list",
                List.of("string_value1", "string_value2")));
    }

    private boolean checkEquals(String propertyName, Object operand)
            throws PropertyException, IOException {
        return new EqualsFilterCriterion(propertyName, operand)
            .check(propertyOwner, propertyManager);
    }

    private boolean checkEqualsWithDefault(String propertyName, Object operand)
            throws PropertyException, IOException {
        return new EqualsFilterCriterion(propertyName, operand)
            .check(propertyOwner, propertyManager);
    }

    private void mockitoPropertyDescriptor(String propertyName, PropertyDescriptor.Type propertyType, boolean isList) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(propertyName)).thenReturn(new PropertyDescriptor(propertyName,
                propertyType, isList, null));
    }

    // Fucking java doesn't allow fucking nulls in fucking List.of...
    @SafeVarargs
    final <T> List<T> listOfFuckingNull(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
    @InjectMocks private PropertyManager propertyManager;

}
