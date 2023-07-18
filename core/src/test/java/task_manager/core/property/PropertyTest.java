package task_manager.core.property;

import org.testng.Assert;
import org.testng.annotations.Test;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;
import task_manager.core.util.Utils;

import java.util.UUID;

import static org.testng.Assert.*;

public class PropertyTest {

        @Test
        public void test_from_nonNull() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "property_value");
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                assertFromEquals("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertFromEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("value1", "value2"));
                assertFromEquals("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(false, true));
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                assertFromEquals("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("value1", "value2"));
                assertFromEquals("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(false, true));
                assertFromEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));
        }

        @Test
        public void test_from_null() throws PropertyException {
                assertFromEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertFromEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
                assertFromEquals("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
                assertFromEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                assertFromEquals("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
                assertFromEquals("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
                assertFromEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);
        }

        @Test
        public void test_from_typeMismatch_singleValue() {
                assertFromThrowsWrongValueTypePropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertFromThrowsWrongValueTypePropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertFromThrowsWrongValueTypePropertyException("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, true);
        }

        @Test
        public void test_from_typeMismatch_multiplicity() {
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, uuid1);
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newLinkedHashSet(uuid1, uuid2));
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, "value");
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newArrayList("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, Utils.newArrayList(true, false));
                assertFromThrowsWrongValueTypePropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, Utils.newLinkedHashSet(true, false));
                assertFromThrowsWrongValueTypePropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, Utils.newArrayList(true, false));
                assertFromThrowsWrongValueTypePropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, Utils.newLinkedHashSet(true, false));
        }

        @Test
        public void test_from_typeMismatch_collectionItem() {
                assertFromThrowsWrongValueTypePropertyException("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                assertFromThrowsWrongValueTypePropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                assertFromThrowsWrongValueTypePropertyException("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("integer_list_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("integer_set_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("value1", "value2"));
                assertFromThrowsWrongValueTypePropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                assertFromThrowsWrongValueTypePropertyException("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
        }

        @Test
        public void test_getStringValue() throws PropertyException {
                assertGetValueWithTypeEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "property_value");
                assertGetValueWithTypeEquals("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
        }

        @Test
        public void test_getStringValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Type.String);
        }

        @Test
        public void test_getStringValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.LIST);
        }

        @Test
        public void test_getUUIDValue() throws PropertyException {
                assertGetValueWithTypeEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                assertGetValueWithTypeEquals("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
        }

        @Test
        public void test_getUUIDValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Type.UUID);
        }

        @Test
        public void test_getUUIDValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getBooleanValue() throws PropertyException {
                assertGetValueWithTypeEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                assertGetValueWithTypeEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, false);
                assertGetValueWithTypeEquals("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
        }

        @Test
        public void test_getBooleanValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Type.Boolean);
        }

        @Test
        public void test_getBooleanValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getIntegerValue() throws PropertyException {
                assertGetValueWithTypeEquals("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, 123);
                assertGetValueWithTypeEquals("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, null);
        }

        @Test
        public void test_getIntegerValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Type.Integer);
        }

        @Test
        public void test_getStringListValue() throws PropertyException {
                assertGetValueWithTypeEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("str1", "str2", null));
                assertGetValueWithTypeEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
        }

        @Test
        public void test_getStringListValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_list_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Type.String);
        }

        @Test
        public void test_getStringListValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Multiplicity.LIST);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.LIST);
        }

        @Test
        public void test_getUUIDListValue() throws PropertyException {
                assertGetValueWithTypeEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2, null));
                assertGetValueWithTypeEquals("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
        }

        @Test
        public void test_getUUIDListValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_list_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Type.UUID);
        }

        @Test
        public void test_getUUIDListValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Multiplicity.LIST);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.LIST);
        }

        @Test
        public void test_getBooleanListValue() throws PropertyException {
                assertGetValueWithTypeEquals("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false, null));
                assertGetValueWithTypeEquals("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
        }

        @Test
        public void test_getBooleanListValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_list_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Type.Boolean);
        }

        @Test
        public void test_getBooleanListValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Multiplicity.LIST);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.LIST);
        }

        @Test
        public void test_getStringSetValue() throws PropertyException {
                assertGetValueWithTypeEquals("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("str1", "str2", null));
                assertGetValueWithTypeEquals("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
        }

        @Test
        public void test_getStringSetValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_set_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Type.String);
        }

        @Test
        public void test_getStringSetValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getUUIDSetValue() throws PropertyException {
                assertGetValueWithTypeEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2, null));
                assertGetValueWithTypeEquals("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);
        }

        @Test
        public void test_getUUIDSetValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_set_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Type.UUID);
        }

        @Test
        public void test_getUUIDSetValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("uuid_list_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("uuid_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getBooleanSetValue() throws PropertyException {
                assertGetValueWithTypeEquals("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false, null));
                assertGetValueWithTypeEquals("boolean_set_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
        }

        @Test
        public void test_getBooleanSetValue_typeMismatch() throws PropertyException {
                assertGetWithTypeThrowsTypeMismatchPropertyException("integer_set_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, PropertyDescriptor.Type.Boolean);
        }

        @Test
        public void test_getBooleanSetValue_wrongMultiplicity() {
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("boolean_list_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, PropertyDescriptor.Multiplicity.SET);
                assertGetWithTypeThrowsWrongMultiplicityPropertyException("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getCollection() throws PropertyException {
                assertGetCollectionEquals("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("str1", "str2"));
        }

        @Test
        public void test_getCollection_wrongMultiplicity_throws() throws PropertyException {
                assertGetCollectionThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
        }

        @Test
        public void test_getList() throws PropertyException {
                assertGetListEquals("string_list_property", PropertyDescriptor.Type.String, Utils.newArrayList("str1", "str2"));
        }

        @Test
        public void test_getList_wrongMultiplicity_throws() throws PropertyException {
                assertGetListThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE);
                assertGetListThrowsWrongMultiplicityPropertyException("string_set_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET);
        }

        @Test
        public void test_getSet() throws PropertyException {
                assertGetSetEquals("string_set_property", PropertyDescriptor.Type.String, Utils.newLinkedHashSet("str1", "str2"));
        }

        @Test
        public void test_getSet_wrongMultiplicity_throws() throws PropertyException {
                assertGetSetThrowsWrongMultiplicityPropertyException("string_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                assertGetSetThrowsWrongMultiplicityPropertyException("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
        }

        private void assertFromEquals(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(getPropertyValue(property, propertyType, multiplicity), propertyValue);
        }

        private void assertFromThrowsWrongValueTypePropertyException(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                assertThrowsWrongValueTypePropertyException(propertyName, propertyType, multiplicity, () ->
                        Property.from(propertyDescriptor, propertyValue)
                );
        }

        private void assertGetWithTypeThrowsTypeMismatchPropertyException(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, PropertyDescriptor.Type requestedPropertyType) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, null);
                assertThrowsTypeMismatchPropertyException(propertyName, propertyType, multiplicity, requestedPropertyType, () ->
                        getPropertyValue(property, requestedPropertyType, multiplicity)
                );
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetCollectionThrowsWrongMultiplicityPropertyException(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertThrowsWrongMultiplicityPropertyException(propertyName, propertyType, multiplicity, property::getCollection);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetListThrowsWrongMultiplicityPropertyException(
                String propertyName,
                PropertyDescriptor.Type propertyType,
                PropertyDescriptor.Multiplicity multiplicity
        ) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, null);
                assertThrowsWrongMultiplicityPropertyException(propertyName, propertyType, multiplicity, property::getList);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetSetThrowsWrongMultiplicityPropertyException(
                String propertyName,
                PropertyDescriptor.Type propertyType,
                PropertyDescriptor.Multiplicity multiplicity,
                Object propertyValue
        ) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertThrowsWrongMultiplicityPropertyException(propertyName, propertyType, multiplicity, property::getSet);
        }

        private void assertGetWithTypeThrowsWrongMultiplicityPropertyException(
                String propertyName,
                PropertyDescriptor.Type propertyType,
                PropertyDescriptor.Multiplicity multiplicity,
                PropertyDescriptor.Multiplicity requestedMultiplicity
        ) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.fromUnchecked(propertyDescriptor, null);
                assertThrowsWrongMultiplicityPropertyException(propertyName, propertyType, multiplicity, () ->
                        getPropertyValue(property, propertyType, requestedMultiplicity)
                );
        }

        private void assertThrowsWrongValueTypePropertyException(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Assert.ThrowingRunnable runnable) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                try {
                        runnable.run();
                        fail();
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongValueType);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), propertyDescriptor);
                        assertEquals(e.getRequestedType(), propertyType);
                } catch (Throwable e) {
                        throw new RuntimeException(e);
                }
        }

        private void assertThrowsTypeMismatchPropertyException(
                String propertyName,
                PropertyDescriptor.Type propertyType,
                PropertyDescriptor.Multiplicity multiplicity,
                PropertyDescriptor.Type requestedPropertyType,
                Assert.ThrowingRunnable runnable) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                try {
                        runnable.run();
                        fail();
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.TypeMismatch);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), propertyDescriptor);
                        assertEquals(e.getRequestedType(), requestedPropertyType);
                } catch (Throwable e) {
                        throw new RuntimeException(e);
                }
        }

        private void assertThrowsWrongMultiplicityPropertyException(
                String propertyName,
                PropertyDescriptor.Type propertyType,
                PropertyDescriptor.Multiplicity multiplicity,
                ThrowingRunnable runnable
        ) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                try {
                        runnable.run();
                        fail();
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.WrongMultiplicity);
                        assertEquals(e.getPropertyName(), propertyName);
                        assertEquals(e.getPropertyDescriptor(), propertyDescriptor);
                } catch (Throwable e) {
                        throw new RuntimeException(e);
                }
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetCollectionEquals(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(property.getCollection(), propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetListEquals(String propertyName, PropertyDescriptor.Type propertyType, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, PropertyDescriptor.Multiplicity.LIST, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(property.getList(), propertyValue);
        }

        @SuppressWarnings("SameParameterValue")
        private void assertGetSetEquals(String propertyName, PropertyDescriptor.Type propertyType, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, PropertyDescriptor.Multiplicity.SET, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                assertEquals(property.getSet(), propertyValue);
        }

        private void assertGetValueWithTypeEquals(String propertyName, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity, Object propertyValue) throws PropertyException {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null, false);
                Property property = Property.from(propertyDescriptor, propertyValue);
                getPropertyValue(property, propertyType, multiplicity);
                assertEquals(getPropertyValue(property, propertyType, multiplicity), propertyValue);
        }

        private Object getPropertyValue(Property property, PropertyDescriptor.Type propertyType, PropertyDescriptor.Multiplicity multiplicity) throws PropertyException {
                if (multiplicity == PropertyDescriptor.Multiplicity.LIST) {
                        if (propertyType == PropertyDescriptor.Type.String) {
                                return property.getStringList();
                        } else if (propertyType == PropertyDescriptor.Type.Boolean) {
                                return property.getBooleanList();
                        } else if (propertyType == PropertyDescriptor.Type.UUID) {
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
                        } else if (propertyType == PropertyDescriptor.Type.Integer) {
                                return property.getInteger();
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
