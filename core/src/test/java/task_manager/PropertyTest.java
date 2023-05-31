package task_manager;

import org.testng.annotations.Test;
import task_manager.property.Property;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class PropertyTest {

        @Test
        public void test_fromRaw_nonNull_successful() throws PropertyException {
                assertFromRawEquals("string_property", PropertyDescriptor.Type.String, false, "property_value", "property_value");
                assertFromRawEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, true, true);
                assertFromRawEquals("uuid_property", PropertyDescriptor.Type.UUID, false, uuid1.toString(), uuid1);
                assertFromRawEquals("uuid_list_property", PropertyDescriptor.Type.UUID, true, List.of(uuid1.toString(), uuid2.toString()), List.of(uuid1, uuid2));
        }

        @Test
        public void test_fromRaw_null_successful() throws PropertyException {
                assertFromRawEquals("string_property", PropertyDescriptor.Type.String, false, null, null);
                assertFromRawEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, null, null);
                assertFromRawEquals("uuid_property", PropertyDescriptor.Type.UUID, false, null, null);
                assertFromRawEquals("uuid_list_property", PropertyDescriptor.Type.UUID, true, null, null);
        }

        @Test
        public void test_fromRaw_throwsPropertyException_typeMismatch() {
                assertFromRawThrowsWrongValueTypePropertyException("string_property", PropertyDescriptor.Type.String, false, 123);
                assertFromRawThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, false, 123);
                assertFromRawThrowsWrongValueTypePropertyException("uuid_property", PropertyDescriptor.Type.UUID, false, 123);
                assertFromRawThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, true, 123);
        }

        @Test
        public void test_from_nonNull_successful() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, false, "property_value");
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, true);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, false, uuid1);
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, true, List.of(uuid1, uuid2));
        }

        @Test
        public void test_from_null_successful() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, false, null);
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, null);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, false, null);
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, true, null);
        }

        @Test
        public void test_from_throwsPropertyException_typeMismatch() {
                assertFromRawThrowsWrongValueTypePropertyException("string_property", PropertyDescriptor.Type.String, false, 123);
                assertFromRawThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, false, 123);
                assertFromRawThrowsWrongValueTypePropertyException("uuid_property", PropertyDescriptor.Type.UUID, false, true);
                assertFromRawThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, true, 123);
                assertFromRawThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, true, 123);
        }

        @Test
        public void test_getValue_successful() throws PropertyException {
                assertGetValueEquals("string_property", PropertyDescriptor.Type.String, false, "property_value");
                assertGetValueEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, true);
                assertGetValueEquals("uuid_property", PropertyDescriptor.Type.UUID, false, uuid1);
                assertGetValueEquals("uuid_list_property", PropertyDescriptor.Type.UUID, true, List.of(uuid1, uuid2));
        }

        @Test
        public void test_getValueRaw_successful() throws PropertyException {
                assertGetRawValueEquals("string_property", PropertyDescriptor.Type.String, false, "property_value");
                assertGetRawValueEquals("boolean_property", PropertyDescriptor.Type.Boolean, false, true);
                assertGetRawValueEquals("uuid_property", PropertyDescriptor.Type.UUID, false, uuid1.toString());
                assertGetRawValueEquals("uuid_list_property", PropertyDescriptor.Type.String, true, List.of(uuid1.toString(), uuid2.toString()));
        }


        private void assertFromRawEquals(String propertyName, PropertyDescriptor.Type propertyType, boolean isList, Object rawPropertyValue, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, isList, null);
                Property property = Property.fromRaw(propertyDescriptor, rawPropertyValue);
                assertEquals(getPropertyValue(property, propertyType, isList), propertyValue);
        }

        private void assertFromEquals(String propertyName, PropertyDescriptor.Type propertyType, boolean isList, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, isList, null);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(getPropertyValue(property, propertyType, isList), propertyValue);
        }

        private void assertFromRawThrowsWrongValueTypePropertyException(String propertyName, PropertyDescriptor.Type propertyType, boolean isList, Object propertyValue) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, isList, null);
                try {
                        Property.fromRaw(propertyDescriptor, propertyValue);
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongValueType);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), propertyDescriptor);
                        assertEquals(e.getRequestedType(), propertyType);
                }
        }

        private void assertGetValueEquals(String propertyName, PropertyDescriptor.Type propertyType, boolean isList, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, isList, null);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(property.getValue(), propertyValue);
        }

        private void assertGetRawValueEquals(String propertyName, PropertyDescriptor.Type propertyType, boolean isList, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, isList, null);
                Property property = Property.fromRaw(propertyDescriptor, propertyValue);
                assertEquals(property.getRawValue(), propertyValue);
        }

        private Object getPropertyValue(Property property, PropertyDescriptor.Type propertyType, boolean isList) throws PropertyException {
                if (isList) {
                        if (propertyType == PropertyDescriptor.Type.UUID) {
                                return property.getUuidList();
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
