package task_manager.ui.cli.command.string_to_property_converter;

import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.core.data.Label;
import task_manager.core.data.OrderedLabel;
import task_manager.core.data.Predicate;
import task_manager.core.property.*;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;
import task_manager.core.util.Utils;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.ui.cli.argument.PropertyArgument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class StringToPropertyConverterTest {

    @BeforeClass public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyDescriptorUseCase);
    }

    @Test
    public void test_stringToProperty_string_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("value"), true), "value");
    }

    @Test
    public void test_stringToProperty_boolean_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("true"), true), true);
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("false"), true), false);
    }

    @Test
    public void test_stringToProperty_uuid_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE), List.of(uuid1.toString()), true), uuid1);
    }

    @Test
    public void test_stringToProperty_integer_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("112"), true), 112);
    }

    @Spy
    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(3);

    @Test
    public void test_stringToProperty_integer_noAssociatedLabel_throws() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.NoAssociatedLabel);
            assertEquals(e.getArgument(), "test");
        }
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Integer, new PropertyDescriptor.IntegerExtra(null), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.NoAssociatedLabel);
            assertEquals(e.getArgument(), "test");
        }
    }

    @Test
    public void test_stringToProperty_stringList_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.LIST), List.of("value1"), true), List.of("value1"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.LIST), List.of("value1", "value2", "value3"), true), List.of("value1", "value2", "value3"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.LIST), List.of("value1", "", "value3"), true), List.of("value1", "", "value3"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.LIST), List.of("", "", ""), true), List.of("", "", ""));
    }

    @Test
    public void test_stringToProperty_booleanList_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.LIST), List.of("true"), true), List.of(true));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.LIST), List.of("true", "false"), true), List.of(true, false));
    }
    @Mock
    private PropertyDescriptorUseCase propertyDescriptorUseCase;

    @Test
    public void test_stringToProperty_uuidList_successful() throws IOException, StringToPropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.LIST),
                List.of(uuid1.toString(), uuid2.toString(), uuid3.toString()), true),
                List.of(uuid1, uuid2, uuid3));
    }
    @Mock
    private LabelUseCase labelUseCase;

    @Test
    public void test_stringToProperty_boolean_invalidBoolean_throws() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("invalid"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.InvalidBoolean);
            assertEquals(e.getArgument(), "invalid");
        }
    }

    @Test
    public void test_stringToProperty_uuid_noAssociatedLabel_throws() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.NoAssociatedLabel);
            assertEquals(e.getArgument(), "test");
        }
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra(null), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.NoAssociatedLabel);
            assertEquals(e.getArgument(), "test");
        }
    }
    @Mock
    private OrderedLabelUseCase orderedLabelUseCase;

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_y_tagCreated() throws IOException, StringToPropertyConverterException {
        Mockito.when(labelUseCase.findLabel("test", "tag")).thenReturn(null);
        Mockito.when(labelUseCase.createLabel(any(), (String) any())).thenReturn(new Label(uuid1, "tag"));
        setStdin("y");

        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag2"), true), uuid1);
    }

    @Test
    public void test_stringToProperty_booleanList_invalidBoolean_throws() throws IOException {
        try {
            assertEquals(propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.LIST), List.of("ttrueee", "false"), true), List.of("value1", "", "value3"));
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.InvalidBoolean);
            assertEquals(e.getArgument(), "ttrueee");
        }
        try {
            assertEquals(propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.LIST), List.of("true", "falllllse"), true), List.of("value1", "", "value3"));
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.InvalidBoolean);
        }
    }

    @Test
    public void test_stringToProperty_emptyList_throws() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of(), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.EmptyList);
            assertEquals(e.getArgument(), "test");
        }
    }

    @Test
    public void test_stringToProperty_notAList_throws() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("true", "false"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.NotAList);
            assertEquals(e.getArgument(), "test");
        }
    }

    @Test
    public void test_convertPredicate_successful() throws StringToPropertyConverterException {
        assertEquals(propertyConverter.parsePredicate("equals"), Predicate.EQUALS);
        assertEquals(propertyConverter.parsePredicate("contains"), Predicate.CONTAINS);
        assertEquals(propertyConverter.parsePredicate("less"), Predicate.LESS);
        assertEquals(propertyConverter.parsePredicate("less_equal"), Predicate.LESS_EQUAL);
        assertEquals(propertyConverter.parsePredicate("greater"), Predicate.GREATER);
        assertEquals(propertyConverter.parsePredicate("greater_equal"), Predicate.GREATER_EQUAL);

    }

    @Test
    public void test_convertPropertiesForFilter() throws IOException, StringToPropertyConverterException, PropertyException {
        mockitoPropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET);

        List<PropertyArgument> properties =  List.of(
                new PropertyArgument(Affinity.NEUTRAL, "boolean_property", "contains", List.of("true")),
                new PropertyArgument(Affinity.NEUTRAL, "integer_property", "less", List.of("123")),
                new PropertyArgument(Affinity.POSITIVE, "string_list_property", "equals", List.of("true", "false")),
                new PropertyArgument(Affinity.NEGATIVE, "uuid_set_property", null, List.of(uuid1.toString(), uuid2.toString()))
        );
        List<FilterPropertySpec> propertySpecs = propertyConverter.convertPropertiesForFiltering(properties, true);
        assertEquals(propertySpecs, List.of(
                new FilterPropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null),
                        true
                ), false, Predicate.CONTAINS),
                new FilterPropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("integer_property", PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE, null),
                        123
                ), false, Predicate.LESS),
                new FilterPropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("string_list_property", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.LIST, null),
                        List.of("true", "false")
                ), false, Predicate.EQUALS),
                new FilterPropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("uuid_set_property", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SET, null),
                        Set.of(uuid1, uuid2)
                ), true, null)
        ));
    }

    @Test
    public void test_convertPropertiesForFilter_invalidPredicate() throws PropertyException, IOException {
        mockitoPropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);

        try {
            List<PropertyArgument> properties =  List.of(
                    new PropertyArgument(Affinity.NEUTRAL, "boolean_property", "invalid_predicate", List.of("true"))
            );
            propertyConverter.convertPropertiesForFiltering(properties, true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.InvalidPredicate);
            assertEquals(e.getArgument(), "invalid_predicate");
        }
    }

    @Test
    public void test_convertPropertiesForModification() throws IOException, StringToPropertyConverterException, PropertyException {
        mockitoPropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("integer_property", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET);

        List<PropertyArgument> properties =  List.of(
                new PropertyArgument(Affinity.NEUTRAL, "boolean_property", null, List.of("true")),
                new PropertyArgument(Affinity.NEUTRAL, "integer_property", null, List.of("123")),
                new PropertyArgument(Affinity.POSITIVE, "string_list_property", null, List.of("true", "false")),
                new PropertyArgument(Affinity.NEGATIVE, "uuid_set_property", null, List.of(uuid1.toString(), uuid2.toString())),
                new PropertyArgument(Affinity.NEUTRAL, "uuid_set_property", "remove", null)
        );
        List<ModifyPropertySpec> propertySpecs = propertyConverter.convertPropertiesForModification(properties, true);

        assertModifyPropertySpecEquals(
                propertySpecs.get(0),
                "boolean_property",
                PropertyDescriptor.Type.Boolean,
                PropertyDescriptor.Multiplicity.SINGLE,
                true,
                ModifyPropertySpec.ModificationType.SET_VALUE,
                null
        );
        assertModifyPropertySpecEquals(
                propertySpecs.get(1),
                "integer_property",
                PropertyDescriptor.Type.Integer,
                PropertyDescriptor.Multiplicity.SINGLE,
                123,
                ModifyPropertySpec.ModificationType.SET_VALUE,
                null
        );
        assertModifyPropertySpecEquals(
                propertySpecs.get(2),
                "string_list_property",
                PropertyDescriptor.Type.String,
                PropertyDescriptor.Multiplicity.LIST,
                Utils.newArrayList("true", "false"),
                ModifyPropertySpec.ModificationType.ADD_VALUES,
                null
        );
        assertModifyPropertySpecEquals(
                propertySpecs.get(3),
                "uuid_set_property",
                PropertyDescriptor.Type.UUID,
                PropertyDescriptor.Multiplicity.SET,
                Utils.newLinkedHashSet(uuid1, uuid2),
                ModifyPropertySpec.ModificationType.REMOVE_VALUES,
                null
        );
        assertModifyPropertySpecEquals(
                propertySpecs.get(4),
                "uuid_set_property",
                PropertyDescriptor.Type.UUID,
                PropertyDescriptor.Multiplicity.SET,
                null,
                ModifyPropertySpec.ModificationType.SET_VALUE,
                ModifyPropertySpec.Option.REMOVE
        );
    }

    @Test
    public void test_convertPropertiesForModification_invalidOption() throws PropertyException, IOException {
        mockitoPropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);

        try {
            List<PropertyArgument> properties =  List.of(
                    new PropertyArgument(Affinity.NEUTRAL, "boolean_property", "invalid_option", null)
            );
            propertyConverter.convertPropertiesForModification(properties, true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.InvalidPropertyOption);
            assertEquals(e.getArgument(), "invalid_option");
        }
    }

    private void assertModifyPropertySpecEquals(
            ModifyPropertySpec modifyPropertySpec,
            String propertyName,
            PropertyDescriptor.Type propertyType,
            PropertyDescriptor.Multiplicity multiplicity,
            Object propertyValue,
            ModifyPropertySpec.ModificationType modificationType,
            ModifyPropertySpec.Option option
    ) {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, propertyType, null, multiplicity, null);
        assertEquals(
                modifyPropertySpec,
                new ModifyPropertySpec(
                        propertyDescriptor,
                        propertyValue != null ? Property.fromUnchecked(propertyDescriptor, propertyValue) : null,
                        modificationType,
                        option));
    }

    @Test
    public void test_stringToProperty_integer_notAnInteger_orderedLabelFound() throws IOException, StringToPropertyConverterException {
        Mockito.when(orderedLabelUseCase.findOrderedLabel("test", "labelText")).thenReturn(new OrderedLabel("labelText", 3));

        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Integer, new PropertyDescriptor.IntegerExtra("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("labelText"), true), 3);
    }

    @Test
    public void test_stringToProperty_integer_notAnInteger_orderedLabelNotFound_throws() throws IOException {
        Mockito.when(orderedLabelUseCase.findOrderedLabel("test", "labelText")).thenReturn(null);

        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Integer, new PropertyDescriptor.IntegerExtra("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("labelText"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.OrderedLabelNotFound);
            assertEquals(e.getArgument(), "test");
        }
    }

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagFound() throws IOException, StringToPropertyConverterException {
        Mockito.when(labelUseCase.findLabel("test", "tag")).thenReturn(new Label(uuid1, "tag"));

        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true), uuid1);
    }

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_n_throws() throws IOException {
        Mockito.when(labelUseCase.findLabel("test", "tag")).thenReturn(null);
        setStdin("n");

        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"), true);
            fail();
        } catch (StringToPropertyConverterException e) {
            assertEquals(e.getExceptionType(), StringToPropertyConverterException.Type.LabelNotFound);
            assertEquals(e.getArgument(), "test");
        }
    }

    private PropertyDescriptor getPropertyDescriptor(PropertyDescriptor.Type type, PropertyDescriptor.Extra extra, PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("test", type, extra, multiplicity, null);
    }

    private void setStdin(String str) {
        System.setIn(new ByteArrayInputStream(str.getBytes()));
    }

    private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) throws IOException, PropertyException {
        Mockito.when(propertyDescriptorUseCase.findPropertyDescriptor(name)).thenReturn(new PropertyDescriptor(name,
                type, null, multiplicity, null));
    }
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();

    @InjectMocks
    StringToPropertyConverter propertyConverter;

}
