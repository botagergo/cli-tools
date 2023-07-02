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
        public void test_addProperty_list() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str2", "str3"));
        }

        @Test
        public void test_addProperty_emptyList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_list", Utils.newArrayList());

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_addProperty_list_toEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList());
                propertyManager.addProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str2", "str3"));
        }

        @Test
        public void test_addProperty_nullList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_list", null);

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_addProperty_list_toNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", null);
                propertyManager.addProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str2", "str3"));
        }

        @Test
        public void test_addProperty_list_toDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.addProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("default_value1", "default_value2", "str2", "str3"));
        }

        @Test
        public void test_addProperty_set() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str2", "str3"));
        }

        @Test
        public void test_addProperty_emptySet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_addProperty_set_toEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());
                propertyManager.addProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str2", "str3"));
        }

        @Test
        public void test_addProperty_nullSet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));
                propertyManager.addProperty(propertyOwner, "test_string_set", null);

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_addProperty_set_toNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", null);
                propertyManager.addProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str2", "str3"));
        }

        @Test
        public void test_addProperty_set_toDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.addProperty(propertyOwner, "test_string_set", Utils.newArrayList("str2", "str3"));

                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("default_value1", "default_value2", "str2", "str3"));
        }

        @Test
        public void test_addProperty_notACollection() throws IOException {
                initPropertyDescriptorsWithoutDefaults();

                try {
                        propertyManager.addProperty(propertyOwner, "test_string", Utils.newArrayList("default_value1"));
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotACollection);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null));
                        assertEquals(e.getPropertyName(), "test_string");
                }
        }

        @Test
        public void test_removeProperty_list() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2", "str3"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removeProperty_list_fromEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList());

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList());
        }

        @Test
        public void test_removeProperty_emptyList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList());
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removeProperty_list_fromNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", null);

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("str2"));
                assertNull(propertyManager.getProperty(propertyOwner, "test_string_list").getValue());
        }

        @Test
        public void test_removeProperty_nullList() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1"));

                propertyManager.removeProperty(propertyOwner, "test_string_list", null);
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1"));
        }

        @Test
        public void test_removeProperty_list_notExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("str4"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str2", "str3"));
        }

        @Test
        public void test_removeProperty_list_oneNotExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_list", Utils.newArrayList("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("str4", "str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("str1", "str3"));
        }

        @Test
        public void test_removeProperty_list_fromDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();

                propertyManager.removeProperty(propertyOwner, "test_string_list", Utils.newArrayList("default_value1"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list").getValue(), Utils.newArrayList("default_value2"));
        }

        @Test
        public void test_removeProperty_notACollection() throws IOException {
                initPropertyDescriptorsWithoutDefaults();

                try {
                        propertyManager.removeProperty(propertyOwner, "test_string", Utils.newArrayList("default_value1"));
                } catch (PropertyException e) {
                        assertEquals(e.getExceptionType(), PropertyException.Type.NotACollection);
                        assertEquals(e.getPropertyDescriptor(), new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null));
                        assertEquals(e.getPropertyName(), "test_string");
                }
        }











        @Test
        public void test_removeProperty_set() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2", "str3"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_set_fromEmpty() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet());
        }

        @Test
        public void test_removeProperty_emptySet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet());
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_set_fromNull() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", null);

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str2"));
                assertNull(propertyManager.getProperty(propertyOwner, "test_string_set").getValue());
        }

        @Test
        public void test_removeProperty_nullSet() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1"));

                propertyManager.removeProperty(propertyOwner, "test_string_set", null);
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1"));
        }

        @Test
        public void test_removeProperty_set_notExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str4"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str2", "str3"));
        }

        @Test
        public void test_removeProperty_set_oneNotExist() throws IOException, PropertyException {
                initPropertyDescriptorsWithoutDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();
                propertyManager.setProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str1", "str2", "str3"));

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("str4", "str2"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("str1", "str3"));
        }

        @Test
        public void test_removeProperty_set_fromDefault() throws IOException, PropertyException {
                initPropertyDescriptorsWithDefaults();

                PropertyOwner propertyOwner = new PropertyOwnerImpl();

                propertyManager.removeProperty(propertyOwner, "test_string_set", Utils.newLinkedHashSet("default_value1"));
                assertEquals(propertyManager.getProperty(propertyOwner, "test_string_set").getValue(), Utils.newLinkedHashSet("default_value2"));
        }

        private void initPropertyDescriptorsWithDefaults() throws IOException {
                mockitoPropertyDescriptor("test_string", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, "default_value");
                mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, true);
                mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE, 1122);
                mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, uuid1);
                mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(true, false));
                mockitoPropertyDescriptor("test_integer_list", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.LIST, true);
                mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST, Utils.newArrayList(uuid1, uuid2));
                mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet("default_value1", "default_value2"));
                mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(true, false));
                mockitoPropertyDescriptor("test_integer_set", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SET, true);
                mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, Utils.newLinkedHashSet(uuid1, uuid2));
        }

        private void initPropertyDescriptorsWithoutDefaults() throws IOException {
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
        }

        private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity, Object defaultValue) throws IOException {
                Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                        type, null, multiplicity, defaultValue));
        }

        private void assertPropertyEquals(String propertyName, Object propertyValue) throws PropertyException, IOException {
                assertEquals(propertyManager.getProperty(propertyOwner, propertyName).getValue(),
                        propertyValue);
        }

        @Mock private PropertyDescriptorRepository propertyDescriptorRepository;
        @Mock private PropertyOwner propertyOwner;
        @InjectMocks PropertyManager propertyManager;
        private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(2);
        private final UUID uuid1 = uuidGenerator.getUUID();
        private final UUID uuid2 = uuidGenerator.getUUID();
}
