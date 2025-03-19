package task_manager.cli_lib.property_to_string_converter;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.cli_lib.DateTimeFormatter;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;
import task_manager.util.Utils;

import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.*;
import static task_manager.property_lib.PropertyDescriptor.Multiplicity.*;

public class DefaultPropertyToStringConverterTest {

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void test_PropertyToString_string() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, null, SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, "str");
        assertEquals(propertyToStringConverter.propertyToString(property), "str");

        property = Property.fromUnchecked(propertyDescriptor, "");
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_stringList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str3", "str2", "str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "str3, str2, str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str3", null, "str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "str3, <null>, str1");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_stringSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str3", "str2", "str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "str1, str2, str3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str3", null, "str1"));
        assertEquals(propertyToStringConverter.propertyToString(property), "<null>, str1, str3");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_integer() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, 123);
        assertEquals(propertyToStringConverter.propertyToString(property), "123");

        property = Property.fromUnchecked(propertyDescriptor, 0);
        assertEquals(propertyToStringConverter.propertyToString(property), "0");

        property = Property.fromUnchecked(propertyDescriptor, -999);
        assertEquals(propertyToStringConverter.propertyToString(property), "-999");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_integerList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3));
        assertEquals(propertyToStringConverter.propertyToString(property), "3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, 2, 1));
        assertEquals(propertyToStringConverter.propertyToString(property), "3, 2, 1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, null, 1));
        assertEquals(propertyToStringConverter.propertyToString(property), "3, <null>, 1");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_integerSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3));
        assertEquals(propertyToStringConverter.propertyToString(property), "3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, 2, 1));
        assertEquals(propertyToStringConverter.propertyToString(property), "1, 2, 3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, null, 1));
        assertEquals(propertyToStringConverter.propertyToString(property), "1, 3, <null>");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_booleanList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true));
        assertEquals(propertyToStringConverter.propertyToString(property), "yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true, false, true));
        assertEquals(propertyToStringConverter.propertyToString(property), "yes, no, yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true, null, false));
        assertEquals(propertyToStringConverter.propertyToString(property), "yes, <null>, no");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_booleanSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true));
        assertEquals(propertyToStringConverter.propertyToString(property), "yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true, false));
        assertEquals(propertyToStringConverter.propertyToString(property), "no, yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true, null, false));
        assertEquals(propertyToStringConverter.propertyToString(property), "<null>, no, yes");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_uuid() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, uuid1);
        assertEquals(propertyToStringConverter.propertyToString(property), uuid1.toString());

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_uuidList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3));
        assertEquals(propertyToStringConverter.propertyToString(property), uuid3.toString());

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3, uuid2, uuid1));
        assertEquals(propertyToStringConverter.propertyToString(property), uuid3 + ", " + uuid2 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3, null, uuid1));
        assertEquals(propertyToStringConverter.propertyToString(property), uuid3 + ", <null>, " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_uuidSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid3));
        assertEquals(propertyToStringConverter.propertyToString(property), uuid3.toString());

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid2, uuid3, uuid1));
        assertEquals(propertyToStringConverter.propertyToString(property), uuid3 + ", " + uuid2 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid3, null, uuid1));
        assertEquals(propertyToStringConverter.propertyToString(property), "<null>, " + uuid3 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    @Test
    public void test_PropertyToString_date() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, "2025-03-20");
        assertEquals(propertyToStringConverter.propertyToString(property), "2025-03-20");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    // TODO
    /* @Test
    public void test_PropertyToString_dateList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertEquals(propertyToStringConverter.propertyToString(property), "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20"));
        assertEquals(propertyToStringConverter.propertyToString(property), "2025-03-20");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20", "1939-01-04", "2300-12-12"));
        assertEquals(propertyToStringConverter.propertyToString(property), "2025-03-20, 1939-01-04, 2300-12-12");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20", null, "2300-12-12"));
        assertEquals(propertyToStringConverter.propertyToString(property), "2025-03-20, <null>, 2300-12-12");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    } */

    @Test
    public void test_PropertyToString_time() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.TimeSubtype(), SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, "17:33:01");
        assertEquals(propertyToStringConverter.propertyToString(property), "5 PM");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertEquals(propertyToStringConverter.propertyToString(property), "");
    }

    private PropertyDescriptor getPropertyDescriptor(
            PropertyDescriptor.Type type,
            PropertyDescriptor.Subtype subtype,
            PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("property", type, subtype, multiplicity, null, false);
    }
    @Mock private LabelUseCase labelUseCase;
    @Mock private OrderedLabelUseCase orderedLabelUseCase;
    private final DefaultPropertyToStringConverter propertyToStringConverter = new DefaultPropertyToStringConverter(
            labelUseCase,
            orderedLabelUseCase,
            new DateTimeFormatter());
    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();
}