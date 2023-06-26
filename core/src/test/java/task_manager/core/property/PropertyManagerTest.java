package task_manager.core.property;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import task_manager.core.TestModule;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;
import task_manager.core.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
                        propertyManager.getProperty("test", propertyOwner);
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
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, 123);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("value1", "value2"));
                mockitoPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(1, 2, 3));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("value1", "value2"));
                mockitoPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(1, 2, 3));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));

                assertPropertyEquals("test_string", "default_value");
                assertPropertyEquals("test_boolean", true);
                assertPropertyEquals("test_integer", 123);
                assertPropertyEquals("test_uuid", uuid1);
                assertPropertyEquals("test_string_list", Utils.newArrayList("value1", "value2"));
                assertPropertyEquals("test_boolean_list", Utils.newArrayList(true, false));
                assertPropertyEquals("test_integer_list", Utils.newArrayList(1, 2, 3));
                assertPropertyEquals("test_uuid_list", Utils.newArrayList(uuid1, uuid2));
                assertPropertyEquals("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                assertPropertyEquals("test_boolean_set", Utils.newLinkedHashSet(true, false));
                assertPropertyEquals("test_integer_set", Utils.newLinkedHashSet(1, 2, 3));
                assertPropertyEquals("test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2));
        }

        @Test
        public void test_getProperty_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, null);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);

                HashMap<String, Object> propertyMap = new HashMap<>();
                propertyMap.put("test_string", "value");
                propertyMap.put("test_boolean", false);
                propertyMap.put("test_integer", 123);
                propertyMap.put("test_uuid", uuid2);
                propertyMap.put("test_string_list", Utils.newArrayList("value1", "value2"));
                propertyMap.put("test_boolean_list", Utils.newArrayList(false, true));
                propertyMap.put("test_integer_list", Utils.newArrayList(1, 2, 3));
                propertyMap.put("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                propertyMap.put("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                propertyMap.put("test_boolean_set", Utils.newLinkedHashSet(false, true));
                propertyMap.put("test_integer_set", Utils.newLinkedHashSet(1, 2, 3));
                propertyMap.put("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));

                Mockito.when(propertyOwner.getProperties()).thenReturn(propertyMap);

                assertPropertyEquals("test_string", "value");
                assertPropertyEquals("test_boolean", false);
                assertPropertyEquals("test_integer", 123);
                assertPropertyEquals("test_uuid", uuid2);
                assertPropertyEquals("test_string_list", Utils.newArrayList("value1", "value2"));
                assertPropertyEquals("test_boolean_list", Utils.newArrayList(false, true));
                assertPropertyEquals("test_integer_list", Utils.newArrayList(1, 2, 3));
                assertPropertyEquals("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                assertPropertyEquals("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                assertPropertyEquals("test_boolean_set", Utils.newLinkedHashSet(false, true));
                assertPropertyEquals("test_integer_set", Utils.newLinkedHashSet(1, 2, 3));
                assertPropertyEquals("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));
        }

        @Test
        public void test_getProperty_defaultExists_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                mockitoPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, true);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                mockitoPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, true);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));

                HashMap<String, Object> propertyMap = new HashMap<>();
                propertyMap.put("test_string", "value");
                propertyMap.put("test_boolean", false);
                propertyMap.put("test_integer", 111);
                propertyMap.put("test_uuid", uuid2);
                propertyMap.put("test_string_list", Utils.newArrayList("value1", "value2"));
                propertyMap.put("test_boolean_list", Utils.newArrayList(false, true));
                propertyMap.put("test_integer_list", Utils.newArrayList(1, 10, 11));
                propertyMap.put("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                propertyMap.put("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                propertyMap.put("test_boolean_set", Utils.newLinkedHashSet(false, true));
                propertyMap.put("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));
                propertyMap.put("test_integer_set", Utils.newLinkedHashSet(1, 10, 11));

                Mockito.when(propertyOwner.getProperties()).thenReturn(propertyMap);

                assertPropertyEquals("test_string", "value");
                assertPropertyEquals("test_boolean", false);
                assertPropertyEquals("test_integer", 111);
                assertPropertyEquals("test_uuid", uuid2);
                assertPropertyEquals("test_string_list", Utils.newArrayList("value1", "value2"));
                assertPropertyEquals("test_boolean_list", Utils.newArrayList(false, true));
                assertPropertyEquals("test_integer_list", Utils.newArrayList(1, 10, 11));
                assertPropertyEquals("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                assertPropertyEquals("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                assertPropertyEquals("test_boolean_set", Utils.newLinkedHashSet(false, true));
                assertPropertyEquals("test_integer_set", Utils.newLinkedHashSet(1, 10, 11));
                assertPropertyEquals("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));
        }

        @Test
        public void test_getProperty_withType_successful() throws PropertyException, IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                mockitoPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, true);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                mockitoPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, true);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));

                HashMap<String, Object> propertyMap = new HashMap<>();
                propertyMap.put("test_string", "value");
                propertyMap.put("test_boolean", false);
                propertyMap.put("test_integer", 123);
                propertyMap.put("test_uuid", uuid2);
                propertyMap.put("test_string_list", Utils.newArrayList("value1", "value2"));
                propertyMap.put("test_boolean_list", Utils.newArrayList(false, true));
                propertyMap.put("test_integer_list", Utils.newArrayList(1, 2, 3));
                propertyMap.put("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                propertyMap.put("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                propertyMap.put("test_boolean_set", Utils.newLinkedHashSet(false, true));
                propertyMap.put("test_integer_set", Utils.newLinkedHashSet(1, 2, 3));
                propertyMap.put("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));

                Mockito.when(propertyOwner.getProperties()).thenReturn(propertyMap);

                assertStringPropertyEquals("test_string", "value");
                assertBooleanPropertyEquals("test_boolean", false);
                assertIntegerPropertyEquals("test_integer", 123);
                assertUUIDPropertyEquals("test_uuid", uuid2);
                assertStringListPropertyEquals("test_string_list", Utils.newArrayList("value1", "value2"));
                assertBooleanListPropertyEquals("test_boolean_list", Utils.newArrayList(false, true));
                assertUUIDListPropertyEquals("test_uuid_list", Utils.newArrayList(uuid2, uuid1));
                assertStringSetPropertyEquals("test_string_set", Utils.newLinkedHashSet("value1", "value2"));
                assertBooleanSetPropertyEquals("test_boolean_set", Utils.newLinkedHashSet(false, true));
                assertUUIDSetPropertyEquals("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid1));
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
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, "value");
                mockitoPropertyDescriptor("test_uuid1", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, 123);
                mockitoPropertyDescriptor("test_uuid2", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, uuid1);
                mockitoPropertyDescriptor("test_uuid3", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1.toString(), uuid2.toString()));
                mockitoPropertyDescriptor("test_uuid4", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, uuid1);
                mockitoPropertyDescriptor("test_uuid5", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1.toString(), uuid2.toString()));
                mockitoPropertyDescriptor("test_string1", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, Utils.newArrayList("value1", "value2"));
                mockitoPropertyDescriptor("test_string2", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, Utils.newLinkedHashSet("value1", "value2"));

                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string", PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.Boolean, "test_boolean", PropertyDescriptor.Multiplicity.SINGLE, "value");
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.Integer, "test_integer", PropertyDescriptor.Multiplicity.SINGLE, "value");
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid1", PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string1", PropertyDescriptor.Multiplicity.SINGLE, Utils.newArrayList("value1", "value2"));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid2", PropertyDescriptor.Multiplicity.LIST, uuid1);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid3", PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1.toString(), uuid2.toString()));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.String, "test_string2", PropertyDescriptor.Multiplicity.SINGLE, Utils.newLinkedHashSet("value1", "value2"));
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid4", PropertyDescriptor.Multiplicity.SET, uuid1);
                assertThrowsWrongTypePropertyException(PropertyDescriptor.Type.UUID, "test_uuid5", PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1.toString(), uuid2.toString()));
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
                        type, null, multiplicity, defaultValue));
        }

        private void assertPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getValue(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertStringPropertyEquals(String propertyName, String propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getString(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertBooleanPropertyEquals(String propertyName, boolean propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getBoolean(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertIntegerPropertyEquals(String propertyName, Integer value) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getInteger(),
                        value);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertUUIDPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getUuid(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertStringListPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getStringList(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertBooleanListPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getBooleanList(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertUUIDListPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getUuidList(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertStringSetPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getStringSet(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertBooleanSetPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getBooleanSet(),
                        propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertUUIDSetPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyName, propertyOwner).getUuidSet(),
                        propertyValue);
        }

        private void getProperty(PropertyDescriptor.Type propertyType, String propertyName, PropertyDescriptor.Multiplicity multiplicity) throws PropertyException, IOException {
                if (multiplicity == PropertyDescriptor.Multiplicity.LIST) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyName, propertyOwner).getUuidList();
                        } else if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyName, propertyOwner).getStringList();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyName, propertyOwner).getBooleanList();
                        }else {
                                throw new RuntimeException();
                        }
                } else if (multiplicity == PropertyDescriptor.Multiplicity.SET) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyName, propertyOwner).getUuidSet();
                        } else if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyName, propertyOwner).getStringSet();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyName, propertyOwner).getBooleanSet();
                        }else {
                                throw new RuntimeException();
                        }
                } else {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                propertyManager.getProperty(propertyName, propertyOwner).getString();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                propertyManager.getProperty(propertyName, propertyOwner).getBoolean();
                        } else if (propertyType == PropertyDescriptor.Type.UUID) {
                                propertyManager.getProperty(propertyName, propertyOwner).getUuid();
                        } else if (propertyType == PropertyDescriptor.Type.Integer) {
                                propertyManager.getProperty(propertyName, propertyOwner).getInteger();
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
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, actualPropertyType, null, PropertyDescriptor.Multiplicity.SINGLE, defaultValue));
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
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, null, multiplicity, defaultValue));
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
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor(propertyName, propertyType, null, actualMultiplicity, null));
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
