package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.label.service.LabelService;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.cli.DateTimeFormatter;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;

import java.io.IOException;
import java.util.UUID;

import static cli_tools.common.property_lib.PropertyDescriptor.Multiplicity.*;
import static org.testng.Assert.*;

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
        assertPropertyToString(property, "str");

        property = Property.fromUnchecked(propertyDescriptor, "");
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_stringList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str1"));
        assertPropertyToString(property, "str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str3", "str2", "str1"));
        assertPropertyToString(property, "str3, str2, str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("str3", null, "str1"));
        assertPropertyToString(property, "str3, <null>, str1");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_stringSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str1"));
        assertPropertyToString(property, "str1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str3", "str2", "str1"));
        assertPropertyToString(property, "str1, str2, str3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet("str3", null, "str1"));
        assertPropertyToString(property, "<null>, str1, str3");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_integer() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, 123);
        assertPropertyToString(property, "123");

        property = Property.fromUnchecked(propertyDescriptor, 0);
        assertPropertyToString(property, "0");

        property = Property.fromUnchecked(propertyDescriptor, -999);
        assertPropertyToString(property, "-999");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_integerList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3));
        assertPropertyToString(property, "3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, 2, 1));
        assertPropertyToString(property, "3, 2, 1");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, null, 1));
        assertPropertyToString(property, "3, <null>, 1");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_integerSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3));
        assertPropertyToString(property, "3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, 2, 1));
        assertPropertyToString(property, "1, 2, 3");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(3, null, 1));
        assertPropertyToString(property, "1, 3, <null>");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_booleanList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true));
        assertPropertyToString(property, "yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true, false, true));
        assertPropertyToString(property, "yes, no, yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(true, null, false));
        assertPropertyToString(property, "yes, <null>, no");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_booleanSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true));
        assertPropertyToString(property, "yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true, false));
        assertPropertyToString(property, "no, yes");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(true, null, false));
        assertPropertyToString(property, "<null>, no, yes");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_uuid() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, uuid1);
        assertPropertyToString(property, uuid1.toString());

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_uuidList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3));
        assertPropertyToString(property, uuid3.toString());

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3, uuid2, uuid1));
        assertPropertyToString(property, uuid3 + ", " + uuid2 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList(uuid3, null, uuid1));
        assertPropertyToString(property, uuid3 + ", <null>, " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_uuidSet() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, SET);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid3));
        assertPropertyToString(property, uuid3.toString());

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid2, uuid3, uuid1));
        assertPropertyToString(property, uuid3 + ", " + uuid2 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, Utils.newLinkedHashSet(uuid3, null, uuid1));
        assertPropertyToString(property, "<null>, " + uuid3 + ", " + uuid1);

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    @Test
    public void test_PropertyToString_date() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, "2025-03-20");
        assertPropertyToString(property, "2025-03-20");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    // TODO
    /* @Test
    public void test_PropertyToString_dateList() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), LIST);

        Property property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList());
        assertPropertyToString(property, "");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20"));
        assertPropertyToString(property, "2025-03-20");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20", "1939-01-04", "2300-12-12"));
        assertPropertyToString(property, "2025-03-20, 1939-01-04, 2300-12-12");

        property = Property.fromUnchecked(propertyDescriptor, Utils.newArrayList("2025-03-20", null, "2300-12-12"));
        assertPropertyToString(property, "2025-03-20, <null>, 2300-12-12");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    } */

    @Test
    public void test_PropertyToString_time() throws IOException {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.TimeSubtype(), SINGLE);

        Property property = Property.fromUnchecked(propertyDescriptor, "17:33:01");
        assertPropertyToString(property, "5 PM");

        property = Property.fromUnchecked(propertyDescriptor, null);
        assertPropertyToString(property, "");
    }

    private void assertPropertyToString(Property property, String expected) throws IOException {
        assertEquals(propertyToStringConverter.propertyToString("property", property), expected);
    }

    private PropertyDescriptor getPropertyDescriptor(
            PropertyDescriptor.Type type,
            PropertyDescriptor.Subtype subtype,
            PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("property", type, subtype, multiplicity, null, null);
    }
    @Mock private LabelService labelService;
    @Mock private OrderedLabelService orderedLabelService;
    private final DefaultPropertyToStringConverter propertyToStringConverter = new DefaultPropertyToStringConverter(
            labelService,
            orderedLabelService,
            new DateTimeFormatter());
    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();
}
