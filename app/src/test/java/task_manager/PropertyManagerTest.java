package task_manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.db.property.PropertyDescriptor;
import task_manager.db.property.PropertyDescriptorCollection;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyManager;
import task_manager.db.property.PropertyOwner;
import static org.testng.Assert.*;

public class PropertyManagerTest {

    @BeforeMethod
    public void clear() {
        propertyDescriptors = new PropertyDescriptorCollection();
        propertyManager = new PropertyManager(propertyDescriptors);
        propertyOwner = new PropertyOwnerImpl(propertyManager);
    }

    @Test
    public void testPropertyNotFound() {
        assertThrows(PropertyException.class,
                () -> propertyManager.getProperty(propertyOwner, "test"));
    }

    @Test
    public void testPropertyDefault() throws PropertyException {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, false, "default_value"));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean",
                PropertyDescriptor.Type.Boolean, false, true));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_uuid",
                PropertyDescriptor.Type.UUID, false, exampleUuid1.toString()));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string_list",
                PropertyDescriptor.Type.String, true, List.of("value1", "value2")));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean_list",
                PropertyDescriptor.Type.Boolean, true, List.of(true, false)));
        propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true,
                        List.of(exampleUuid1.toString(), exampleUuid2.toString())));

        assertEquals(propertyManager.getProperty(propertyOwner, "test_string"), "default_value");
        assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean"), true);
        assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid"), exampleUuid1);
        assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list"),
                List.of("value1", "value2"));
        assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean_list"),
                List.of(true, false));
        assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid_list"),
                List.of(exampleUuid1, exampleUuid2));
    }

    @Test
    public void testPropertyExists() throws PropertyException {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, false, "default_value"));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean",
                PropertyDescriptor.Type.Boolean, false, true));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_uuid",
                PropertyDescriptor.Type.UUID, false, exampleUuid1.toString()));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string_list",
                PropertyDescriptor.Type.String, true, List.of("default_value1", "default_value2")));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean_list",
                PropertyDescriptor.Type.Boolean, true, List.of(true, false)));
        propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true,
                        List.of(exampleUuid1.toString(), exampleUuid2.toString())));

        propertyOwner.getProperties().put("test_string", "value");
        propertyOwner.getProperties().put("test_boolean", false);
        propertyOwner.getProperties().put("test_uuid", exampleUuid2.toString());
        propertyOwner.getProperties().put("test_string_list", List.of("value1", "value2"));
        propertyOwner.getProperties().put("test_boolean_list", List.of(false, true));
        propertyOwner.getProperties().put("test_uuid_list",
                List.of(exampleUuid2.toString(), exampleUuid1.toString()));

        assertEquals(propertyManager.getProperty(propertyOwner, "test_string"), "value");
        assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean"), false);
        assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid"), exampleUuid2);
        assertEquals(propertyManager.getProperty(propertyOwner, "test_string_list"),
                List.of("value1", "value2"));
        assertEquals(propertyManager.getProperty(propertyOwner, "test_boolean_list"),
                List.of(false, true));
        assertEquals(propertyManager.getProperty(propertyOwner, "test_uuid_list"),
                List.of(exampleUuid2, exampleUuid1));
    }

    @Test
    public void testPropertyExistsWithType() throws PropertyException {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, false, "default_value"));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean",
                PropertyDescriptor.Type.Boolean, false, true));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_uuid",
                PropertyDescriptor.Type.UUID, false, exampleUuid1.toString()));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string_list",
                PropertyDescriptor.Type.String, true, List.of("default_value1", "default_value2")));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean_list",
                PropertyDescriptor.Type.Boolean, true, List.of(true, false)));
        propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, true,
                        List.of(exampleUuid1.toString(), exampleUuid2.toString())));

        propertyOwner.getProperties().put("test_string", "value");
        propertyOwner.getProperties().put("test_boolean", false);
        propertyOwner.getProperties().put("test_uuid", exampleUuid2.toString());
        propertyOwner.getProperties().put("test_string_list", List.of("value1", "value2"));
        propertyOwner.getProperties().put("test_boolean_list", List.of(false, true));
        propertyOwner.getProperties().put("test_uuid_list",
                List.of(exampleUuid2.toString(), exampleUuid1.toString()));

        assertEquals(propertyManager.getStringProperty(propertyOwner, "test_string"), "value");
        assertEquals(propertyManager.getBooleanProperty(propertyOwner, "test_boolean"), false);
        assertEquals(propertyManager.getUuidProperty(propertyOwner, "test_uuid"), exampleUuid2);
        assertEquals(propertyManager.getUuidListProperty(propertyOwner, "test_uuid_list"),
                List.of(exampleUuid2, exampleUuid1));
    }

    @Test
    public void testPropertyTypeMismatch() {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, false, "default_value"));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean",
                PropertyDescriptor.Type.Boolean, false, true));

        assertThrows(PropertyException.class,
                () -> propertyOwner.getBooleanProperty("test_string"));
        assertThrows(PropertyException.class,
                () -> propertyOwner.getStringProperty("test_boolean"));
        assertThrows(PropertyException.class, () -> propertyOwner.getUuidProperty("test_boolean"));
    }

    @Test
    public void testWrongType() {
        propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor("test_string", PropertyDescriptor.Type.String, false, 123));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_boolean",
                PropertyDescriptor.Type.Boolean, false, "value"));
        propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, false, 123));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string1",
                PropertyDescriptor.Type.String, false, List.of("value1", "value2")));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string2",
                PropertyDescriptor.Type.String, true, "value"));
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string3",
                PropertyDescriptor.Type.String, true, List.of(1, 2, 3)));

        assertThrows(PropertyException.class,
                () -> propertyOwner.getBooleanProperty("test_string"));
        assertThrows(PropertyException.class,
                () -> propertyOwner.getStringProperty("test_boolean"));
        assertThrows(PropertyException.class, () -> propertyOwner.getUuidProperty("test_uuid"));
        assertThrows(PropertyException.class, () -> propertyOwner.getUuidProperty("test_string1"));
        assertThrows(PropertyException.class, () -> propertyOwner.getUuidProperty("test_string2"));
        assertThrows(PropertyException.class, () -> propertyOwner.getUuidProperty("test_string3"));
    }

    @Test
    public void testNotAList() {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_uuid",
                PropertyDescriptor.Type.UUID, false, exampleUuid1));

        assertThrows(PropertyException.class, () -> propertyOwner.getUuidListProperty("test_uuid"));
    }

    @Test
    public void testIsAList() {
        propertyDescriptors.addPropertyDescriptor(new PropertyDescriptor("test_string",
                PropertyDescriptor.Type.String, true, "default_value"));

        assertThrows(PropertyException.class, () -> propertyOwner.getStringProperty("test_string"));
    }

    PropertyDescriptorCollection propertyDescriptors;
    PropertyManager propertyManager;
    private PropertyOwner propertyOwner;

    private UUID exampleUuid1 = UUID.randomUUID();
    private UUID exampleUuid2 = UUID.randomUUID();
}


class PropertyOwnerImpl extends PropertyOwner {

    public PropertyOwnerImpl(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
        this.properties = new HashMap<>();
    }

    @Override
    public PropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Map<String, Object> properties;

    private PropertyManager propertyManager;

}
