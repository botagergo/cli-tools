package cli_tools.common.property_converter;

import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.core.repository.LabelRepositoryFactory;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_lib.PropertyDescriptor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class PropertyConverterTest {

    @BeforeClass public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyDescriptorService);
    }

    @Test
    public void test_convertProperty_string_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE), List.of("value")), List.of("value"));
    }

    @Test
    public void test_convertProperty_boolean_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of(true)), List.of(true));
    }

    @Test
    public void test_convertProperty_integer_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE), List.of(123)), List.of(123));
    }

    @Test
    public void test_convertProperty_uuid_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of(uuid1)), List.of(uuid1));
    }

    @Test
    public void test_convertProperty_list_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST), List.of(uuid1.toString(), uuid2.toString(), uuid3.toString())),
                List.of(uuid1, uuid2, uuid3));
    }

    @Test
    public void test_convertProperty_set_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                        getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET), List.of(uuid1.toString(), uuid2.toString(), uuid3.toString())),
                Utils.newLinkedHashSet(uuid1, uuid2, uuid3));
    }

    @Test
    public void test_convertProperty_uuid_notAnUuid_tagFound() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(new Label(uuid1, "tag"));

        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag")), List.of(uuid1));
    }


    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_throws() throws IOException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(null);

        try {
            propertyConverter.convertProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"));
            Assert.fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.LabelNotFound);
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE));
            assertEquals(e.getPropertyValue(), "tag");
        }
    }

    private PropertyDescriptor getPropertyDescriptor(PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("test", type, null, multiplicity, null, null);
    }

    @Mock private LabelRepositoryFactory labelRepositoryFactory;
    @Mock private PropertyDescriptorService propertyDescriptorService;
    @Mock private LabelRepository labelRepository;
    @Spy private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(3);
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();
    private final UUID uuid3 = uuidGenerator.getUUID();

    @InjectMocks
    PropertyConverter propertyConverter;

}
