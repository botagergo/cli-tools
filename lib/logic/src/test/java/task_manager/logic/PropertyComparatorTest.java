package task_manager.logic;

import org.testng.annotations.Test;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.logic.PropertyComparator;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyDescriptor;

import static org.testng.Assert.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PropertyComparatorTest {

    @Test
    public void test_compare_string() {
        assertTrue(nullsFirstPropertyComparator.compare(getStringProperty("str1"), getStringProperty("str2")) < 0);
        assertTrue(nullsFirstPropertyComparator.compare(getStringProperty("str1"), getStringProperty("str0")) > 0);
        assertEquals(nullsFirstPropertyComparator.compare(getStringProperty("str1"), getStringProperty("str1")), 0);
    }

    @Test
    public void test_compare_integer() {
        assertTrue(nullsFirstPropertyComparator.compare(getIntegerProperty(3), getIntegerProperty(5)) < 0);
        assertTrue(nullsFirstPropertyComparator.compare(getIntegerProperty(5), getIntegerProperty(3)) > 0);
        assertEquals(nullsFirstPropertyComparator.compare(getIntegerProperty(4), getIntegerProperty(4)), 0);
    }

    @Test
    public void test_compare_boolean() {
        assertTrue(nullsFirstPropertyComparator.compare(getBooleanProperty(false), getBooleanProperty(true)) < 0);
        assertTrue(nullsFirstPropertyComparator.compare(getBooleanProperty(true), getBooleanProperty(false)) > 0);
        assertEquals(nullsFirstPropertyComparator.compare(getBooleanProperty(true), getBooleanProperty(true)), 0);
    }

    @Test
    public void test_compare_two_nulls() {
        assertEquals(nullsFirstPropertyComparator.compare(getStringProperty(null), getStringProperty(null)), 0);
        assertEquals(nullsLastPropertyComparator.compare(getStringProperty(null), getStringProperty(null)), 0);
    }

    @Test
    public void test_compare_first_null() {
        assertTrue(nullsFirstPropertyComparator.compare(getStringProperty(null), getStringProperty("str")) < 0);
        assertTrue(nullsLastPropertyComparator.compare(getStringProperty(null), getStringProperty("str")) > 0);
    }

    @Test
    public void test_compare_second_null() {
        assertTrue(nullsFirstPropertyComparator.compare(getStringProperty("str"), getStringProperty(null)) > 0);
        assertTrue(nullsLastPropertyComparator.compare(getStringProperty("str"), getStringProperty(null)) < 0);
    }

    @Test
    public void test_wrongType_throws() {
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getStringProperty("str"), getUuidProperty()));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getUuidProperty(), getStringProperty("str")));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getUuidProperty(), getUuidProperty()));
    }

    @Test
    public void test_wrongMultiplicity_throws() {
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getListProperty(), getStringProperty("str")));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getStringProperty("str"), getListProperty()));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getListProperty(), getListProperty()));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getSetProperty(), getStringProperty("str")));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getStringProperty("str"), getSetProperty()));
        assertThrows(RuntimeException.class, () -> nullsFirstPropertyComparator.compare(getSetProperty(), getSetProperty()));
    }

    private Property getStringProperty(String value) {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_string",
                        PropertyDescriptor.Type.String,
                        null, PropertyDescriptor.Multiplicity.SINGLE,
                        null, false),
                value);
    }

    private Property getIntegerProperty(Integer value) {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_integer",
                        PropertyDescriptor.Type.Integer,
                        null, PropertyDescriptor.Multiplicity.SINGLE,
                        null, false),
                value);
    }

    private Property getBooleanProperty(Boolean value) {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_boolean",
                        PropertyDescriptor.Type.Boolean,
                        null, PropertyDescriptor.Multiplicity.SINGLE,
                        null, false),
                value);
    }

    private Property getUuidProperty() {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_uuid",
                        PropertyDescriptor.Type.UUID,
                        null, PropertyDescriptor.Multiplicity.SINGLE,
                        null, false),
                uuidGenerator.getUUID());
    }

    private Property getListProperty() {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_list",
                        PropertyDescriptor.Type.String,
                        null, PropertyDescriptor.Multiplicity.LIST,
                        null, false),
                null);
    }

    private Property getSetProperty() {
        return Property.fromUnchecked(
                new PropertyDescriptor(
                        "test_set",
                        PropertyDescriptor.Type.String,
                        null, PropertyDescriptor.Multiplicity.SET,
                        null, false),
                null);
    }


    final PropertyComparator nullsFirstPropertyComparator = new PropertyComparator(true);
    final PropertyComparator nullsLastPropertyComparator = new PropertyComparator(false);
    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();

}
