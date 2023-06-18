package task_manager;

import org.testng.annotations.Test;
import task_manager.property.Property;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class PropertyTest {

        @Test
        public void test_from_nonNull_successful() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "property_value");
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                assertFromEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of("value1", "value2"));
                assertFromEquals("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, List.of(false, true));
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1, uuid2));
                assertFromEquals("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of("value1", "value2")));
                assertFromEquals("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(false, true)));
                assertFromEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(uuid1, uuid2)));
        }

        @Test
        public void test_from_null_successful() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                assertFromEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);
        }

        @Test
        public void test_from_throwsPropertyException_typeMismatch_singleValue() {
                assertFromThrowsWrongValueTypePropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertFromThrowsWrongValueTypePropertyException("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, List.of("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, new LinkedHashSet<>(List.of("value1", "value2")));
        }

        @Test
        public void test_from_throwsPropertyException_typeMismatch_multiplicity() {
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, uuid1);
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, new LinkedHashSet<>(List.of(uuid1, uuid2)));
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, "value");
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, List.of("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, List.of(true, false));
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, new LinkedHashSet<>(List.of(true, false)));
        }

        @Test
        public void test_from_throwsPropertyException_typeMismatch_collectionItem() {
                assertFromThrowsWrongValueTypePropertyException("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, List.of(true, false));
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(true, false)));
                assertFromThrowsWrongValueTypePropertyException("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, List.of("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of("value1", "value2")));
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(true, false));
                assertFromThrowsWrongValueTypePropertyException("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(List.of(true, false)));
        }

        @Test
        public void test_getValue_successful() throws PropertyException {
                assertGetValueEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "property_value");
                assertGetValueEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertGetValueEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                assertGetValueEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, List.of(uuid1, uuid2));
        }

        private void assertFromEquals(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(getPropertyValue(property, propertyType, multiplicity), propertyValue);
        }

        private void assertFromThrowsWrongValueTypePropertyException(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null);
                try {
                        Property.from(propertyDescriptor, propertyValue);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongValueType);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), propertyDescriptor);
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        private void assertGetValueEquals(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(property.getValue(), propertyValue);
        }

        private Object getPropertyValue(Property property, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity) throws PropertyException {
                if (multiplicity == PropertyDescriptor.Multiplicity.LIST) {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                return property.getStringList();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                return property.getBooleanList();
                        } if (propertyType == PropertyDescriptor.Type.UUID) {
                                return property.getUuidList();
                        } else {
                                throw new RuntimeException();
                        }
                } else if (multiplicity == PropertyDescriptor.Multiplicity.SET) {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                return property.getStringSet();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                return property.getBooleanSet();
                        } if (propertyType == PropertyDescriptor.Type.UUID) {
                                return property.getUuidSet();
                        } else {
                                throw new RuntimeException();
                        }
                } else {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                return property.getString();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                return property.getBoolean();
                        } else if (propertyType == PropertyDescriptor.Type.UUID) {
                                return property.getUuid();
                        } else {
                                throw new RuntimeException();
                        }
                }
        }

        private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(2);
        private final UUID uuid1 = uuidGenerator.getUUID();
        private final UUID uuid2 = uuidGenerator.getUUID();
}
