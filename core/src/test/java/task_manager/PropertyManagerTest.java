package task_manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        public void test_getProperty_notFound_throwsPropertyException() {
                assertThrows(PropertyException.class, () -> propertyManager.getProperty(propertyOwner, "test"));
        }

        @Test
        public void test_getProperty_notDefined_returnsDefault() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, uuid1.toString());
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true, List.of("value1", "value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, true, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true, List.of(uuid1.toString(), uuid2.toString()));

                assertProperty("test_string", "default_value");
                assertProperty("test_boolean", true);
                assertProperty("test_uuid", uuid1.toString());
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(true, false));
                assertProperty("test_uuid_list", List.of(uuid1.toString(), uuid2.toString()));
        }

        @Test
        public void test_getProperty_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, null);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, null);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, null);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true, null);
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, true, null);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true, null);

                Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2.toString(),
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2.toString(), uuid1.toString())
                )));

                assertProperty("test_string", "value");
                assertProperty("test_boolean", false);
                assertProperty("test_uuid", uuid2.toString());
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(false, true));
                assertProperty("test_uuid_list", List.of(uuid2.toString(), uuid1.toString()));
        }

        @Test
        public void test_getProperty_defaultExists_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, uuid1.toString());
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true, List.of("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, true, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true, List.of(uuid1.toString(), uuid2.toString()));

                Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2.toString(),
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2.toString(), uuid1.toString())
                )));

                assertProperty("test_string", "value");
                assertProperty("test_boolean", false);
                assertProperty("test_uuid", uuid2.toString());
                assertProperty("test_string_list", List.of("value1", "value2"));
                assertProperty("test_boolean_list", List.of(false, true));
                assertProperty("test_uuid_list", List.of(uuid2.toString(), uuid1.toString()));
        }

        @Test
        public void test_getProperty_withType_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, uuid1.toString());
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, true, List.of("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, true, List.of(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true, List.of(uuid1.toString(), uuid2.toString()));

                Mockito.when(propertyOwner.getRawProperties()).thenReturn(new HashMap<>(Map.of(
                        "test_string", "value",
                        "test_boolean", false,
                        "test_uuid", uuid2.toString(),
                        "test_string_list", List.of("value1", "value2"),
                        "test_boolean_list", List.of(false, true),
                        "test_uuid_list", List.of(uuid2.toString(), uuid1.toString())
                )));

                assertStringProperty();
                assertBooleanProperty();
                assertUUIDProperty(uuid2);
                assertUUIDListProperty(List.of(uuid2, uuid1));
        }

        @Test
        public void test_getProperty_withType_typeMismatch_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, true);

                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Type.String, "test_string", "default_value");
                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.String, PropertyDescriptor.Type.Boolean, "test_boolean", true);
                assertThrowsMismatchPropertyException(PropertyDescriptor.Type.UUID, PropertyDescriptor.Type.Boolean, "test_boolean", true);
        }

        @Test
        public void test_getProperty_withType_wrongType_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, 123);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, "value");
                mockitoPropertyDescriptor("test_uuid1", PropertyDescriptor.Type.UUID, false, 123);
                mockitoPropertyDescriptor("test_uuid2", PropertyDescriptor.Type.UUID, true, uuid1.toString());
                mockitoPropertyDescriptor("test_uuid3", PropertyDescriptor.Type.UUID, true, List.of(uuid1.toString(), uuid2.toString()));
                mockitoPropertyDescriptor("test_string1", PropertyDescriptor.Type.String, false, List.of("value1", "value2"));

                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string", false, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.Boolean, "test_boolean", false, "value");
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid1", false, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string1", false, List.of("value1", "value2"));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid2", true, uuid1.toString());
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid3", true, List.of(uuid1.toString(), uuid2.toString()));
        }

        @Test
        public void test_getProperty_notAList_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, false, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, uuid1.toString());

                assertThrowsNotAListPropertyException(uuid1.toString());
        }

        @Test
        public void test_getProperty_isAList_throwsPropertyException() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, true, null);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, true, null);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, true, null);

                assertThrowsIsAListPropertyException(PropertyDescriptor.Type.String, "test_string");
                assertThrowsIsAListPropertyException(PropertyDescriptor.Type.Boolean, "test_boolean");
                assertThrowsIsAListPropertyException(PropertyDescriptor.Type.UUID, "test_uuid");
        }

        private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, boolean isList, Object defaultValue) throws IOException {
                Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                        type, isList, defaultValue));
        }

        private void assertProperty(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, propertyName).getRawValue(),
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

        private void assertUUIDListProperty(Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid_list").getUuidList(),
                        propertyValue);
        }

        private void getProperty(PropertyDescriptor.Type propertyType, String propertyName, boolean isList) throws PropertyException, IOException {
                if (isList) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyOwner, propertyName).getUuidList();
                        } else {
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
                        getProperty(requestedPropertyType, propertyName, false);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.TypeMismatch);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, actualPropertyType, false, defaultValue));
                        assertEquals(e.getRequestedType(), requestedPropertyType);
                }
        }

        private void assertThrowsWrongTypePropertyException(
                PropertyDescriptor.Type propertyType,
                String propertyName,
                boolean isList,
                Object defaultValue) throws IOException {
                try {
                        getProperty(propertyType, propertyName, isList);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongValueType);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, isList, defaultValue));
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        private void assertThrowsNotAListPropertyException(
                Object defaultValue) throws IOException {
                try {
                        getProperty(PropertyDescriptor.Type.UUID, "test_uuid", true);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotAList);
                        assertEquals(e.getPropertyName(), "test_uuid");
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, defaultValue));
                        assertEquals(e.getRequestedType(), PropertyDescriptor.Type.UUID);
                }
        }

        private void assertThrowsIsAListPropertyException(
                PropertyDescriptor.Type propertyType,
                String propertyName) throws IOException {
                try {
                        getProperty(propertyType, propertyName, false);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.IsAList);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, true, null));
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
        @Mock private PropertyOwner propertyOwner;
        @InjectMocks PropertyManager propertyManager;
        private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
        private final UUID uuid1 = uuidGenerator.getUUID();
        private final UUID uuid2 = uuidGenerator.getUUID();
}
