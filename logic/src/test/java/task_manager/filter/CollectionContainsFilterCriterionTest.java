package task_manager.filter;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Sets;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.Utils;
import task_manager.logic.filter.CollectionContainsFilterCriterion;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CollectionContainsFilterCriterionTest {

    public CollectionContainsFilterCriterionTest() {
        MockitoAnnotations.openMocks(this);

        propertyManager = new PropertyManager();
        addPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST);
        addPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET);
        addPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET);
        addPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET);
    }

    @BeforeMethod
    public void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    public void test_check_listContains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", "value3"),
                "test_boolean_list", Utils.newArrayList(true, false, true),
                "test_uuid_list", Utils.newArrayList(uuid1, uuid2, uuid3)
        ));
        assertTrue(new CollectionContainsFilterCriterion("test_string_list", List.of("value3", "value2")).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_boolean_list", List.of(true, true)).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_uuid_list", List.of(uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_setContains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", "value3"),
                "test_boolean_set", Utils.newLinkedHashSet(true, false, true),
                "test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2, uuid3)
        ));
        assertTrue(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet("value3", "value2")).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(true, true)).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_listDoesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", "value3"),
                "test_boolean_list", Utils.newArrayList(true, false, true),
                "test_uuid_list", Utils.newArrayList(uuid1, uuid2, uuid3)
        ));
        assertFalse(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList("value4", "value2")).check(propertyOwner, propertyManager));
        assertFalse(new CollectionContainsFilterCriterion("test_boolean_list", Utils.newArrayList(true, null)).check(propertyOwner, propertyManager));
        assertFalse(new CollectionContainsFilterCriterion("test_uuid_list", Utils.newArrayList(uuid4)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_setDoesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", "value3"),
                "test_boolean_set", Utils.newLinkedHashSet(true, false, true),
                "test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2, uuid3)
        ));
        assertFalse(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet("value4", "value2")).check(propertyOwner, propertyManager));
        assertFalse(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(true, null)).check(propertyOwner, propertyManager));
        assertFalse(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(uuid4)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_listContainsNull() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", null),
                "test_boolean_list", Utils.newArrayList(true, null, true),
                "test_uuid_list", Utils.newArrayList(null, uuid2, uuid3)
        ));
        assertTrue(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList(null, "value2")).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_boolean_list", Utils.newArrayList(null, true)).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_uuid_list", Utils.newArrayList(null, uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_setContainsNull() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", null),
                "test_boolean_set", Utils.newLinkedHashSet(true, null),
                "test_uuid_set", Utils.newLinkedHashSet(null, uuid2, uuid3)
        ));
        assertTrue(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet(null, "value2")).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(null)).check(propertyOwner, propertyManager));
        assertTrue(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(null, uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_listContainsEmpty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("value1", "value2")));
        assertTrue(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList()).check(propertyOwner, propertyManager));
    }

    @Test
    public void test_check_setContainsEmpty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_set", Utils.newLinkedHashSet("value1", "value2")));
        assertTrue(new CollectionContainsFilterCriterion("test_string_set", Sets.newLinkedHashSet()).check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                type, null, multiplicity, null, false));
    }

    @Mock
    private PropertyOwner propertyOwner;
    @InjectMocks
    private PropertyManager propertyManager;
    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    final UUID uuid1 = uuidGenerator.getUUID();
    final UUID uuid2 = uuidGenerator.getUUID();
    final UUID uuid3 = uuidGenerator.getUUID();
    final UUID uuid4 = uuidGenerator.getUUID();
}
