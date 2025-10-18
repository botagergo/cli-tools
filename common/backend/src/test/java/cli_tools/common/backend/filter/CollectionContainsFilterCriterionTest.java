package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.util.Utils;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CollectionContainsFilterCriterionTest {

    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    final UUID uuid1 = uuidGenerator.getUUID();
    final UUID uuid2 = uuidGenerator.getUUID();
    final UUID uuid3 = uuidGenerator.getUUID();
    final UUID uuid4 = uuidGenerator.getUUID();
    @Mock
    private PropertyOwner propertyOwner;
    @InjectMocks
    private PropertyManager propertyManager;

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
    void clear() {
        Mockito.reset(propertyOwner);
    }

    @Test
    void test_listContains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", "value3"),
                "test_boolean_list", Utils.newArrayList(true, false, true),
                "test_uuid_list", Utils.newArrayList(uuid1, uuid2, uuid3)
        ));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_list", List.of("value3", "value2")).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_boolean_list", List.of(true, true)).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_uuid_list", List.of(uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_setContains() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", "value3"),
                "test_boolean_set", Utils.newLinkedHashSet(true, false, true),
                "test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2, uuid3)
        ));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet("value3", "value2")).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(true, true)).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_listDoesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", "value3"),
                "test_boolean_list", Utils.newArrayList(true, false, true),
                "test_uuid_list", Utils.newArrayList(uuid1, uuid2, uuid3)
        ));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList("value4", "value2")).check(propertyOwner, propertyManager));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_boolean_list", Utils.newArrayList(true, null)).check(propertyOwner, propertyManager));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_uuid_list", Utils.newArrayList(uuid4)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_setDoesNotContain() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", "value3"),
                "test_boolean_set", Utils.newLinkedHashSet(true, false, true),
                "test_uuid_set", Utils.newLinkedHashSet(uuid1, uuid2, uuid3)
        ));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet("value4", "value2")).check(propertyOwner, propertyManager));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(true, null)).check(propertyOwner, propertyManager));
        Assert.assertFalse(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(uuid4)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_listContainsNull() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_list", Utils.newArrayList("value1", "value2", null),
                "test_boolean_list", Utils.newArrayList(true, null, true),
                "test_uuid_list", Utils.newArrayList(null, uuid2, uuid3)
        ));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList(null, "value2")).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_boolean_list", Utils.newArrayList(null, true)).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_uuid_list", Utils.newArrayList(null, uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_setContainsNull() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap(
                "test_string_set", Utils.newLinkedHashSet("value1", "value2", null),
                "test_boolean_set", Utils.newLinkedHashSet(true, null),
                "test_uuid_set", Utils.newLinkedHashSet(null, uuid2, uuid3)
        ));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_set", Utils.newLinkedHashSet(null, "value2")).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_boolean_set", Utils.newLinkedHashSet(null)).check(propertyOwner, propertyManager));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_uuid_set", Utils.newLinkedHashSet(null, uuid2, uuid3)).check(propertyOwner, propertyManager));
    }

    @Test
    void test_listContainsEmpty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_list", Utils.newArrayList("value1", "value2")));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_list", Utils.newArrayList()).check(propertyOwner, propertyManager));
    }

    @Test
    void test_setContainsEmpty() throws PropertyException, IOException {
        Mockito.when(propertyOwner.getProperties()).thenReturn(Utils.newHashMap("test_string_set", Utils.newLinkedHashSet("value1", "value2")));
        Assert.assertTrue(new CollectionContainsFilterCriterion("test_string_set", Sets.newLinkedHashSet()).check(propertyOwner, propertyManager));
    }

    private void addPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) {
        propertyManager.getPropertyDescriptorCollection().addPropertyDescriptor(new PropertyDescriptor(name,
                type, null, multiplicity, null, null));
    }
}
