package task_manager.property_lib;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;
import task_manager.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Guice(modules = TestModule.class)
public class PropertyManagerTest {

        public PropertyManagerTest() {
                propertyManager = new PropertyManager();
                propertyManager.setPropertyDescriptorCollection(new PropertyDescriptorCollection());
        }

        @BeforeClass
        public void initMocks() {
                MockitoAnnotations.openMocks(this);
        }

        @BeforeMethod
        public void clear() {
                Mockito.reset(propertyOwner);
                propertyManager.getPropertyDescriptorCollection().clear();
        }

        @Test
        public void test_getProperty_notFound() throws IOException {
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
                initPropertyDescriptorsWithDefaults();

                assertPropertyEquals("test_string", "default_value");
                assertPropertyEquals("test_boolean", true);
                assertPropertyEquals("test_integer", 1122);
                assertPropertyEquals("test_uuid", uuid1);
                assertPropertyEquals("test_string_list", Utils.newArrayList("default_value1", "default_value2"));
                assertPropertyEquals("test_boolean_list", Utils.newArrayList(true, false));
                assertPropertyEquals("test_uuid_list", Utils.newArrayList(uuid1, uuid2));
                assertPropertyEquals("test_string_set", Utils.newLinkedHashSet("default_value1", "default_value2"));
                assertPropertyEquals("test_boolean_set", Utils.newLinkedHashSet(true, false));
                assertPropertyEquals("test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2));
        }

        @Test
        public void test_getProperty() throws PropertyException, IOException {
                initPropertyDescriptorsWithoutDefaults();

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
        public void test_getProperty_defaultExists() throws PropertyException, IOException {
                initPropertyDescriptorsWithDefaults();

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
        public void test_setProperty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string", "str1");

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string").getValue(), "str1");
        }

        @Test
        public void test_addPropertyValues_list() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_listWithNulls() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList(null));
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", null));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList(null, "str2", null));
        }

        @Test
        public void test_addPropertyValues_emptyList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList());

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_addPropertyValues_list_toEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList());
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_nullList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", null);

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_addPropertyValues_list_toNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", null);
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_list_toDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.addPropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("default_value1", "default_value2", "str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_set() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_setWithNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", null));
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", null));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet(null, "str1", "str2"));
        }

        @Test
        public void test_addPropertyValues_emptySet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet());

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_addPropertyValues_set_toEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_nullSet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", null);

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_addPropertyValues_set_toNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", null);
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_set_toDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.addPropertyValues(propertyOwner, "test_string_set", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("default_value1", "default_value2", "str2", "str3"));
        }

        @Test
        public void test_addPropertyValues_notACollection() throws IOException {
                initPropertyDescriptorsWithoutDefaults();

                try {
                        propertyManager.addPropertyValues(propertyOwner, "test_string", Utils.newArrayList("default_value1"));
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotACollection);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
                        assertEquals(e.getPropertyName(), "test_string");
                }
        }

        @Test
        public void test_removePropertyValues_list() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removePropertyValues_listWithNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", null));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2", null));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removePropertyValues_list_fromEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList());

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList());
        }

        @Test
        public void test_removePropertyValues_emptyList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList());
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removePropertyValues_list_fromNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", null);

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str2"));
                assertNull(propertyManager.getProperty(propertyOwner, "test_string_list").getValue());
        }

        @Test
        public void test_removePropertyValues_nullList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", null);
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removePropertyValues_list_notExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str4"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str2", "str3"));
        }

        @Test
        public void test_removePropertyValues_list_oneNotExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("str4", "str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str3"));
        }

        @Test
        public void test_removePropertyValues_list_fromDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();

                propertyManager.removePropertyValues(propertyOwner, "test_string_list", Utils.newArrayList("default_value1"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("default_value2"));
        }

        @Test
        public void test_removePropertyValues_notACollection() throws IOException {
                initPropertyDescriptorsWithoutDefaults();

                try {
                        propertyManager.removePropertyValues(propertyOwner, "test_string", Utils.newArrayList("default_value1"));
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotACollection);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null, false));
                        assertEquals(e.getPropertyName(), "test_string");
                }
        }

        @Test
        public void test_removeProperty_set() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_setWithNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", null, "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", null));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str3"));
        }

        @Test
        public void test_removeProperty_set_fromEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet());
        }

        @Test
        public void test_removeProperty_emptySet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet());
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_set_fromNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", null);

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2"));
                assertNull(propertyManager.getProperty(propertyOwner, "test_string_set").getValue());
        }

        @Test
        public void test_removeProperty_nullSet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", null);
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_set_notExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str4"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str2", "str3"));
        }

        @Test
        public void test_removeProperty_set_oneNotExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str4", "str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str3"));
        }

        @Test
        public void test_removeProperty_set_fromDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();

                propertyManager.removePropertyValues(propertyOwner, "test_string_set", Utils.newLinkedHashSet("default_value1"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("default_value2"));
        }

        private void initPropertyDescriptorsWithDefaults() {
                addPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                addPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                addPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, 1122);
                addPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                addPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("default_value1", "default_value2"));
                addPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                addPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, true);
                addPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                addPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("default_value1", "default_value2"));
                addPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                addPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, true);
                addPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));
        }

        private void initPropertyDescriptorsWithoutDefaults() {
                addPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null);
                addPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null);
                addPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, null);
                addPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null);
                addPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null);
                addPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, null);
                addPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, null);
                addPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, null);
                addPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, null);
                addPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, null);
                addPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, null);
                addPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null);
        }

        private void addPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity, Object defaultValue) {
                propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                        type, null, multiplicity, defaultValue, false));
        }

        private void assertPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, propertyName).getValue(),
                        propertyValue);
        }

        @Mock private PropertyOwner propertyOwner;
        @InjectMocks
        PropertyManager propertyManager;
        private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(2);
        private final UUID uuid1 = uuidGenerator.getUUID();
        private final UUID uuid2 = uuidGenerator.getUUID();
}
