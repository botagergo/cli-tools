package task_manager;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.core.data.SortingCriterion;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.core.util.Utils;
import task_manager.logic.PropertyNotComparableException;
import task_manager.logic.sorter.PropertySorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertThrows;

public class PropertySorterTest {

    @BeforeClass
    public void init() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockitoPropertyDescriptor("test_string1", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_string2", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_string_list", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("test_string_set", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SET);
        mockitoPropertyDescriptor("test_boolean", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_boolean_list", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("test_boolean_set", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SET);
        mockitoPropertyDescriptor("test_integer", PropertyDescriptor.Type.Integer, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE);
        mockitoPropertyDescriptor("test_uuid_list", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.LIST);
        mockitoPropertyDescriptor("test_uuid_set", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET);
    }

    @Test
    public void test_PropertySorter_sort_strings() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_string1", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2"))
        );

        assertSorted(propertyOwners, sorter, List.of(1, 2, 0));
    }

    @Test
    public void test_PropertySorter_sort_booleans() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_boolean", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", false))
        );

        assertSorted(propertyOwners, sorter, List.of(1, 0));
    }

    @Test
    public void test_PropertySorter_sort_integers() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_integer", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_integer", 3)),
                new PropertyOwnerImpl(Utils.newHashMap("test_integer", 4)),
                new PropertyOwnerImpl(Utils.newHashMap("test_integer", -9999)),
                new PropertyOwnerImpl(Utils.newHashMap("test_integer", 11))
        );

        assertSorted(propertyOwners, sorter, List.of(2, 0, 1, 3));
    }

    @Test
    public void test_PropertySorter_sort_stringsWithDuplicate() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_string1", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2"))
        );

        assertSorted(propertyOwners, sorter, List.of(1, 2, 4, 0, 3));
    }

    @Test
    public void test_PropertySorter_sort_stringsWithNulls() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_string1", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", null)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", null))
        );

        assertSorted(propertyOwners, sorter, List.of(1, 3, 2, 0));
    }

    @Test
    public void test_PropertySorter_sort_booleansWithDuplicate() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_boolean", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", false)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", false))
        );

        assertSorted(propertyOwners, sorter, List.of(1, 4, 0, 2, 3));
    }

    @Test
    public void test_PropertySorter_sort_booleansWithNulls() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_boolean", true)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", false)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", null)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", null)),
                new PropertyOwnerImpl(Utils.newHashMap("test_boolean", false))
        );

        assertSorted(propertyOwners, sorter, List.of(2, 3, 1, 4, 0));
    }

    @Test
    public void test_PropertySorter_sort_descending() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_string1", false)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1")),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2"))
        );

        assertSorted(propertyOwners, sorter, List.of(0, 2, 1));
    }

    @Test
    public void test_PropertySorter_sort_multipleCriteria() throws IOException, PropertyException, PropertyNotComparableException {
        PropertySorter<PropertyOwnerImpl> sorter = new PropertySorter<>(List.of(
                new SortingCriterion("test_string1", false),
                new SortingCriterion("test_string2", true),
                new SortingCriterion("test_boolean", false)
        ));

        ArrayList<PropertyOwnerImpl> propertyOwners = Utils.newArrayList(
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3", "test_string2", "str1", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1", "test_string2", "str3", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3", "test_string2", "str3", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2", "test_string2", "str2", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2", "test_string2", "str2", "test_boolean", false)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str3", "test_string2", "str3", "test_boolean", false)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2", "test_string2", "str3", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1", "test_string2", null, "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str1", "test_string2", "str1", "test_boolean", true)),
                new PropertyOwnerImpl(Utils.newHashMap("test_string1", "str2", "test_string2", "str1", "test_boolean", true))
        );

        assertSorted(propertyOwners, sorter, List.of(0, 2, 5, 9, 3, 4, 6, 7, 8, 1));
    }

    @Test
    public void test_PropertySorter_sort_propertyNotComparable() {
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_string_list", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_string_set", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_boolean_list", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_boolean_set", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_uuid", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_uuid_list", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
        assertThrows(PropertyNotComparableException.class, () ->
                new PropertySorter<PropertyOwnerImpl>(List.of(new SortingCriterion("test_uuid_set", false)))
                        .sort(Utils.newArrayList(new PropertyOwnerImpl()), propertyManager));
    }

    private void assertSorted(ArrayList<PropertyOwnerImpl> propertyOwners, PropertySorter<PropertyOwnerImpl> sorter, List<Integer> order) throws PropertyException, IOException, PropertyNotComparableException {
        assertOrder(propertyOwners, sorter.sort(propertyOwners, propertyManager), order);
    }

    private void assertOrder(List<PropertyOwnerImpl> propertyOwners, List<PropertyOwnerImpl> sortedPropertyOwners, List<Integer> order) {
        for (int i = 0; i < order.size(); i++) {
            Assert.assertEquals(sortedPropertyOwners.get(i), propertyOwners.get(order.get(i)));
        }
    }

    private void mockitoPropertyDescriptor(String name, PropertyDescriptor.Type type, PropertyDescriptor.Multiplicity multiplicity) throws IOException {
        Mockito.when(propertyDescriptorRepository.get(name)).thenReturn(new PropertyDescriptor(name,
                type, null, multiplicity, null));
    }

    @Mock
    private PropertyDescriptorRepository propertyDescriptorRepository;

    @InjectMocks
    private PropertyManager propertyManager;


}
