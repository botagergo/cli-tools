package task_manager;

import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.data.Label;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.view.PropertyConverter;
import task_manager.logic.use_case.view.PropertyConverterException;
import task_manager.property.PropertyDescriptor;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;
import task_manager.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class PropertyConverterTest {

    @BeforeClass public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyDescriptorUseCase);
    }

    @Test
    public void test_convertProperty_string_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE), List.of("value")), "value");
    }

    @Test
    public void test_convertProperty_boolean_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of(true)), true);
    }

    @Test
    public void test_convertProperty_uuid_successful() throws IOException, PropertyConverterException {
        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of(uuid1)), uuid1);
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
    public void test_convertProperty_emptyList_throws() throws IOException {
        try {
            propertyConverter.convertProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of());
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.EmptyList);
            assertNull(e.getPropertyValue());
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE));
        }
    }

    @Test
    public void test_convertProperty_notACollection_throws() throws IOException {
        try {
            propertyConverter.convertProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE), List.of("true", "false"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.NotACollection);
            assertNull(e.getPropertyValue());
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE));
        }
    }

    @Test
    public void test_convertProperty_uuid_notAnUuid_tagFound() throws IOException, PropertyConverterException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(new Label(uuid1, "tag"));

        assertEquals(propertyConverter.convertProperty(
                getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag")), uuid1);
    }


    @Test
    public void test_stringToProperty_uuid_notAnUuid_tagNotFound_n_throwsPropertyConverterException() throws IOException {
        Mockito.when(labelRepositoryFactory.getLabelRepository("test")).thenReturn(labelRepository);
        Mockito.when(labelRepository.find("tag")).thenReturn(null);

        try {
            propertyConverter.convertProperty(
                    getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE), List.of("tag"));
            fail();
        } catch (PropertyConverterException e) {
            assertEquals(e.getExceptionType(), PropertyConverterException.Type.LabelNotFound);
            assertEquals(e.getPropertyDescriptor(), getPropertyDescriptor(PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE));
            assertEquals(e.getPropertyValue(), "tag");
        }
    }

    private PropertyDescriptor getPropertyDescriptor(PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) {
        return new PropertyDescriptor("test", type, null, multiplicity, null);
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
