package cli_tools.common.backend.property_converter;

import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.core.repository.LabelRepositoryFactory;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class PropertyConverterTest {

    @Spy
    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(3);
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();
    @Mock
    private PropertyDescriptorService propertyDescriptorService;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private OrderedLabelService orderedLabelService;
    @Mock
    private LabelRepositoryFactory labelRepositoryFactory;
    @InjectMocks
    private PropertyConverter propertyConverter;

    @BeforeClass
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeMethod
    void clear() {
        Mockito.reset(propertyDescriptorService);
    }

    @Test
    void test_convertProperty_string_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("value")), List.of("value"));
    }

    @Test
    void test_convertProperty_boolean_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of(true)), List.of(true));
    }

    @Test
    void test_convertProperty_integer_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE),
                List.of(123)), List.of(123));
    }

    @Test
    void test_convertProperty_uuid_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE), List.of(uuid1)), List.of(uuid1));
    }

    @Test
    void test_convertProperty_list_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.LIST), List.of(uuid1.toString(), uuid2.toString(), uuid3.toString())),
                List.of(uuid1, uuid2, uuid3));
    }

    @Test
    void test_convertProperty_set_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SET), List.of(uuid1.toString(), uuid2.toString(), uuid3.toString())),
                Utils.newLinkedHashSet(uuid1, uuid2, uuid3));
    }

    @Test
    void test_convertProperty_integer_string_labelFound() throws IOException, PropertyConverterException {
        Mockito.when(orderedLabelService.findOrderedLabel("test", "label1")).thenReturn(new OrderedLabel("label1", 3));
        assertEquals(propertyConverter.convertProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("test"), PropertyDescriptor.Multiplicity.SINGLE),
                        List.of("label1")),
                List.of(3));
    }

    @Test
    void test_convertProperty_integer_string_labelNotFound_throws() throws IOException {
        Mockito.when(orderedLabelService.findOrderedLabel("test", "label1")).thenReturn(null);

        assertThrows(PropertyConverterException.class, () ->
                propertyConverter.convertProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("label1")));
    }

    @Test
    void test_convertProperty_labelExists() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.exists("test", "tag")).thenReturn(true);

        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.LabelSubtype("test"), PropertyDescriptor.Multiplicity.SINGLE), List.of("tag")), List.of("tag"));
    }

    @Test
    void test_stringToProperty_labelNotExists() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.exists("test", "tag")).thenReturn(false);

        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag1")), List.of("tag1"));
    }

    private PropertyDescriptor getPropertyDescriptor(PropertyDescriptor.Type type, PropertyDescriptor.Subtype subtype, PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("test", type, subtype, multiplicity, null, null);
    }

}
