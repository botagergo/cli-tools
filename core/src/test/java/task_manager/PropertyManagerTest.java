package task_manager;

import java.io.IOException;
import java.util.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;
import task_manager.repository.PropertyDescriptorRepository;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

import static org.testng.Assert.*;

@Guice(modules = TestModule.class)
public class PropertyManagerTest {

        @BeforeClass
        public void initMocks() {
                MockitoAnnotations.openMocks(this);
        }

        @BeforeMethod
        public void clear() {
                Mockito.reset(propertyOwner);
                Mockito.reset(propertyDescriptorRepository);
        }

        @Test
        public void test_getProperty_notFound_throwsPropertyException() throws IOException {
                try {
                        propertyManager.getProperty(propertyOwner, "test");
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotExist);
                        assertEquals(e.getPropertyName(), "test");
                        assertNull(e.getPropertyDescriptor());
                        assertNull(e.getRequestedType());
                }
        }

        @Test
        public void test_getProperty_notDefined_returnsDefault() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of("value1", "value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of("value1", "value2")));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(true, false)));
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1, uuid2)));

                assertProperty("test_string", "default_value");
                assertProperty("test_boolean", true);
                assertProperty("test_uuid", uuid1);
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(true, false));
                assertProperty("test_uuid_list", List.of(uuid1, uuid2));
                assertProperty("test_string_set", new LinkedHashSet<>(List.of("value1", "value2")));
                assertProperty("test_boolean_set", new LinkedHashSet<>(List.of(true, false)));
                assertProperty("test_uuid_set", new LinkedHashSet<>(List.of(uuid1, uuid2)));
        }

        @Test
        public void test_getProperty_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);

                Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2,
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2, uuid1),
                        "test_string_set", new LinkedHashSet<>(List.of("value1", "value2")),
                        "test_boolean_set", new LinkedHashSet<>(List.of(false, true)),
                        "test_uuid_set", new LinkedHashSet<>(List.of(uuid2, uuid1))
                )));

                assertProperty("test_string", "value");
                assertProperty("test_boolean", false);
                assertProperty("test_uuid", uuid2);
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(false, true));
                assertProperty("test_uuid_list", List.of(uuid2, uuid1));
                assertProperty("test_string_set", new LinkedHashSet<>(List.of("value1", "value2")));
                assertProperty("test_boolean_set", new LinkedHashSet<>(List.of(false, true)));
                assertProperty("test_uuid_set", new LinkedHashSet<>(List.of(uuid2, uuid1)));
        }

        @Test
        public void test_getProperty_defaultExists_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of("default_value1", "default_value2")));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(true, false)));
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1, uuid2)));

                Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2,
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2, uuid1),
                        "test_string_set", new LinkedHashSet<>(List.of("value1", "value2")),
                        "test_boolean_set", new LinkedHashSet<>(List.of(false, true)),
                        "test_uuid_set", new LinkedHashSet<>(List.of(uuid2, uuid1))
                )));

                assertProperty("test_string", "value");
                assertProperty("test_boolean", false);
                assertProperty("test_uuid", uuid2);
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(false, true));
                assertProperty("test_uuid_list", List.of(uuid2, uuid1));
                assertProperty("test_string_set", new LinkedHashSet<>(List.of("value1", "value2")));
                assertProperty("test_boolean_set", new LinkedHashSet<>(List.of(false, true)));
                assertProperty("test_uuid_set", new LinkedHashSet<>(List.of(uuid2, uuid1)));
        }

        @Test
        public void test_getProperty_withType_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of("default_value1", "default_value2")));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(true, false)));
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1, uuid2)));

                Mockito.when(propertyOwner.getProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2,
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2, uuid1),
                        "test_string_set", new LinkedHashSet<>(List.of("value1", "value2")),
                        "test_boolean_set", new LinkedHashSet<>(List.of(false, true)),
                        "test_uuid_set", new LinkedHashSet<>(List.of(uuid2, uuid1))
                )));

                assertStringProperty();
                assertBooleanProperty();
                assertUUIDProperty(uuid2);
                assertStringListProperty(List.of("value1", "value2"));
                assertBooleanListProperty(List.of(false, true));
                assertUUIDListProperty(List.of(uuid2, uuid1));
                assertStringSetProperty(new LinkedHashSet<>(List.of("value1", "value2")));
                assertBooleanSetProperty(new LinkedHashSet<>(List.of(false, true)));
                assertUUIDSetProperty(new LinkedHashSet<>(List.of(uuid2, uuid1)));
        }

        @Test
        public void test_getProperty_withType_typeMismatch_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);

                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Type.String, "test_string", "default_value");
                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.String, PropertyDescriptor.Type.Boolean, "test_boolean", true);
                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.UUID, PropertyDescriptor.Type.Boolean, "test_boolean", true);
        }

        @Test
        public void test_getProperty_withType_wrongType_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, 123);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, "value");
                mockitoPropertyDescriptor("test_uuid1", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, 123);
                mockitoPropertyDescriptor("test_uuid2", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, uuid1);
                mockitoPropertyDescriptor("test_uuid3", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1.toString(), uuid2.toString()));
                mockitoPropertyDescriptor("test_uuid4", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, uuid1);

                mockitoPropertyDescriptor("test_uuid5", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1.toString(), uuid2.toString())));

                mockitoPropertyDescriptor("test_string1", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, List.of("value1", "value2"));
                mockitoPropertyDescriptor("test_string2", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, new LinkedHashSet<>(List.of("value1", "value2")));

                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string", PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.Boolean, "test_boolean", PropertyDescriptor.Multiplicity.SINGLE, "value");
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid1", PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string1", PropertyDescriptor.Multiplicity.SINGLE, List.of("value1", "value2"));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid2", PropertyDescriptor.Multiplicity.LIST, uuid1);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid3", PropertyDescriptor.Multiplicity.LIST, List.of(uuid1.toString(), uuid2.toString()));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string2", PropertyDescriptor.Multiplicity.SINGLE, new LinkedHashSet<>(List.of("value1", "value2")));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid4", PropertyDescriptor.Multiplicity.SET, uuid1);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid5", PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1.toString(), uuid2.toString())));
        }

        @Test
        public void test_getProperty_wrongMultiplicity_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);

                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.String, "test_string", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SINGLE);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.Boolean, "test_boolean", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SINGLE);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.UUID, "test_uuid", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SINGLE);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.String, "test_string_set", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.Boolean, "test_boolean_set", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.UUID, "test_uuid_set", PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.String, "test_string_set", PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.Boolean, "test_boolean_set", PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
                assertThrowsWrongMultiplicityPropertyException(PropertyDescriptor.Type.UUID, "test_uuid_set", PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity, Object defaultValue) throws IOException {
                Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                        type, multiplicity, defaultValue));
        }

        private void assertProperty(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, propertyName).getValue(),
                        propertyValue);
        }

        private void assertStringProperty() throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string").getString(),
                        (Object) "value");
        }

        private void assertBooleanProperty() throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean").getBoolean(),
                        (Object) false);
        }

        private void assertUUIDProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid").getUuid(),
                        propertyValue);
        }

        private void assertStringListProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getStringList(),
                        propertyValue);
        }

        private void assertBooleanListProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean_list").getBooleanList(),
                        propertyValue);
        }

        private void assertUUIDListProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid_list").getUuidList(),
                        propertyValue);
        }

        private void assertStringSetProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getStringSet(),
                        propertyValue);
        }

        private void assertBooleanSetProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean_set").getBooleanSet(),
                        propertyValue);
        }

        private void assertUUIDSetProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid_set").getUuidSet(),
                        propertyValue);
        }

        private void getProperty(PropertyDescriptor.Type propertyType, String propertyName, PropertyDescriptor.Multiplicity multiplicity) throws PropertyException, IOException {
                if (multiplicity == PropertyDescriptor.Multiplicity.LIST) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyOwner, propertyName).getUuidList();
                        } else if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyOwner, propertyName).getStringList();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyOwner, propertyName).getBooleanList();
                        }else {
                                throw new RuntimeException();
                        }
                } else if (multiplicity == PropertyDescriptor.Multiplicity.SET) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyOwner, propertyName).getUuidSet();
                        } else if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyOwner, propertyName).getStringSet();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyOwner, propertyName).getBooleanSet();
                        }else {
                                throw new RuntimeException();
                        }
                } else {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyOwner, propertyName).getString();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyOwner, propertyName).getBoolean();
                        } else if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyOwner, propertyName).getUuid();
                        } else {
                                throw new RuntimeException();
                        }
                }
        }

        private void assertThrowsMismatchPropertyException(
                PropertyDescriptor.Type requestedPropertyType,
                PropertyDescriptor.Type actualPropertyType,
                String propertyName,
                Object defaultValue) throws IOException {
                try {
                        getProperty(requestedPropertyType, propertyName, PropertyDescriptor.Multiplicity.SINGLE);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.TypeMismatch);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, actualPropertyType, PropertyDescriptor.Multiplicity.SINGLE, defaultValue));
                        assertEquals(e.getRequestedType(), requestedPropertyType);
                }
        }

        private void assertThrowsWrongTypePropertyException(
                PropertyDescriptor.Type propertyType,
                String propertyName,
                PropertyDescriptor.Multiplicity multiplicity,
                Object defaultValue) throws IOException {
                try {
                        getProperty(propertyType, propertyName, multiplicity);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongValueType);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, multiplicity, defaultValue));
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        private void assertThrowsWrongMultiplicityPropertyException(
                PropertyDescriptor.Type propertyType,
                String propertyName,
                PropertyDescriptor.Multiplicity requestedMultiplicity,
                PropertyDescriptor.Multiplicity actualMultiplicity) throws IOException {
                try {
                        getProperty(propertyType, propertyName, requestedMultiplicity);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongMultiplicity);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, actualMultiplicity, null));
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
        @Mock private PropertyOwner propertyOwner;
        @InjectMocks PropertyManager propertyManager;
        private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(2);
        private final UUID uuid1 = uuidGenerator.getUUID();
        private final UUID uuid2 = uuidGenerator.getUUID();
}
