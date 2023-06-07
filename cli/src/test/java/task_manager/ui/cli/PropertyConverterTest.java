package task_manager.ui.cli;

import org.apache.commons.lang3.tuple.Triple;
import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.data.Label;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.property.Property;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertySpec;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.ui.cli.command.property_converter.PropertyConverter;
import task_manager.ui.cli.command.property_converter.PropertyConverterException;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

import static org.mockito.ArgumentMatchers.any;
import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PropertyConverterTest {

    @BeforeClass public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyDescriptorUseCase);
    }

    @Test
    public void test_stringToProperty_string_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE), List.of("value")), "value");
    }

    @Test
    public void test_stringToProperty_boolean_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of("true")), true);
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of("false")), false);
    }

    @Test
    public void test_stringToProperty_boolean_invalidBoolean_throwsPropertyConverterException() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of("invalid"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.InvalidBoolean);
            assertEquals(e.getPropertyValue(), "invalid");
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE));
        }
    }

    @Test
    public void test_stringToProperty_uuid_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of(uuid1.toString())), uuid1);
    }

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagFound() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(new Label(uuid1, "tag"));

        assertEquals(propertyConverter.stringToProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag")), uuid1);
    }

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_y_tagCreated() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(null);
        Mockito.when(labelRepository.create(any())).thenReturn(new Label(uuid1, "tag"));
        setStdin("y");

        assertEquals(propertyConverter.stringToProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag2")), uuid1);
    }

    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_n_throwsPropertyConverterException() throws IOException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(null);
        setStdin("n");

        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.LabelNotFound);
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE));
            assertEquals(e.getPropertyValue(), "tag");
        }
    }

    @Test
    public void test_stringToProperty_stringList_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST), List.of("value1")), List.of("value1"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST), List.of("value1", "value2", "value3")), List.of("value1", "value2", "value3"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST), List.of("value1", "", "value3")), List.of("value1", "", "value3"));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST), List.of("", "", "")), List.of("", "", ""));
    }

    @Test
    public void test_stringToProperty_booleanList_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST), List.of("true")), List.of(true));
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST), List.of("true", "false")), List.of(true, false));
    }

    @Test
    public void test_stringToProperty_booleanList_invalidBoolean_throwsPropertyConverterException() throws IOException {
        try {
            assertEquals(propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST), List.of("ttrueee", "false")), List.of("value1", "", "value3"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.InvalidBoolean);
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST));
            assertEquals(e.getPropertyValue(), "ttrueee");
        }
        try {
            assertEquals(propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST), List.of("true", "falllllse")), List.of("value1", "", "value3"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.InvalidBoolean);
        }
    }

    @Test
    public void test_stringToProperty_uuidList_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.stringToProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST),
                List.of(uuid1.toString(), uuid2.toString(), uuid3.toString())),
                List.of(uuid1, uuid2, uuid3));
    }

    @Test
    public void test_stringToProperty_emptyList_throwsPropertyConverterException() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of());
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.EmptyList);
            assertNull(e.getPropertyValue());
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE));
        }
    }

    @Test
    public void test_stringToProperty_notAList_throwsPropertyConverterException() throws IOException {
        try {
            propertyConverter.stringToProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of("true", "false"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.NotAList);
            assertNull(e.getPropertyValue());
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE));
        }
    }

    @Test
    public void test_convertProperties() throws IOException, PropertyConverterException, PropertyException {
        mockitoPropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET);

        List<Triple<PropertySpec.Affinity, String, List<String>>> properties =  List.of(
                Triple.of(PropertySpec.Affinity.NEUTRAL, "boolean_property", List.of("true")),
                Triple.of(PropertySpec.Affinity.POSITIVE, "string_list_property", List.of("true", "false")),
                Triple.of(PropertySpec.Affinity.NEGATIVE, "uuid_set_property", List.of(uuid1.toString(), uuid2.toString()))
        );
        List<PropertySpec> propertySpecs = propertyConverter.convertProperties(properties);
        assertEquals(propertySpecs, List.of(
                new PropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("boolean_property", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, null),
                        true
                ), PropertySpec.Affinity.NEUTRAL),
                new PropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("string_list_property", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST, null),
                        List.of("true", "false")
                ), PropertySpec.Affinity.POSITIVE),
                new PropertySpec(Property.fromUnchecked(
                        new PropertyDescriptor("uuid_set_property", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null),
                        Set.of(uuid1, uuid2)
                ), PropertySpec.Affinity.NEGATIVE)
        ));
    }

    private PropertyDescriptor getPropertyDescriptor(PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("test", type, multiplicity, null);
    }

    private void setStdin(String str) {
        System.setIn(new ByteArrayInputStream(str.getBytes()));
    }

    private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) throws IOException, PropertyException {
        Mockito.when(propertyDescriptorUseCase.getPropertyDescriptor(name)).thenReturn(new PropertyDescriptor(name,
                type, multiplicity, null));
    }

    @Mock private LabelRepositoryFactory labelRepositoryFactory;
    @Mock private PropertyDescriptorUseCase propertyDescriptorUseCase;
    @Mock private LabelRepository labelRepository;
    @Spy private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(3);
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();

    @InjectMocks
    PropertyConverter propertyConverter;

}
