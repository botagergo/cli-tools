package task_manager.logic.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.core.data.SortingCriterion;
import task_manager.logic.PropertyComparator;
import task_manager.logic.PropertyNotComparableException;
import task_manager.property_lib.*;

import java.io.IOException;
import java.util.*;

//@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@Setter
@Getter
@JsonSerialize
@JsonDeserialize
@NoArgsConstructor
public class PropertySorter <T extends PropertyOwner> {

    @JsonCreator
    public PropertySorter(@JsonProperty("sortingCriteria") List<SortingCriterion> sortingCriteria) {
        this.sortingCriteria = sortingCriteria;
    }

    public void sort(List<T> propertyOwners, PropertyManager propertyManager) throws PropertyException, IOException, PropertyNotComparableException {
        List<Pair<List<Property>, Boolean>> valuesToCompare = new ArrayList<>();

        for (SortingCriterion sortingCriterion : sortingCriteria) {
            PropertyDescriptor propertyDescriptor = propertyManager.getPropertyDescriptor(sortingCriterion.propertyName());
            if (!PropertyComparator.isComparable(propertyDescriptor)) {
                throw new PropertyNotComparableException(propertyDescriptor);
            }

            Pair<List<Property>, Boolean> values = Pair.of(new ArrayList<>(), sortingCriterion.ascending());
            for (T propertyOwner : propertyOwners) {
                Property propertyValue = propertyManager.getProperty(propertyOwner, propertyDescriptor);
                values.getLeft().add(propertyValue);
            }
            valuesToCompare.add(values);
        }

        ListIndexComparator comp = new ListIndexComparator(valuesToCompare);

        List<Integer> indices = comp.createIndexArray();
        indices.sort(comp);

        reorder(propertyOwners, indices);
    }

    void reorder(List<T> list, List<Integer> indices)
    {
        List<T> reorderedList = new ArrayList<>(Collections.nCopies(list.size(), null));
        for (int i = 0; i < list.size(); i++) {
            int ind = indices.get(i);
            reorderedList.set(i, list.get(ind));
        }

        for (int i = 0; i < list.size(); i++) {
            list.set(i, reorderedList.get(i));
        }
    }

    private List<SortingCriterion> sortingCriteria;

    private static class ListIndexComparator implements Comparator<Integer> {

        public ListIndexComparator(List<Pair<List<Property>, Boolean>> values) {
            this.values = values;
        }

        @Override
        public int compare(Integer index1, Integer index2) {
            for (Pair<List<Property>, Boolean> criterion : values) {
                int compResult = Objects.compare(criterion.getLeft().get(index1), criterion.getLeft().get(index2), propertyComparator);
                if (compResult != 0) {
                    if (!criterion.getRight()) {
                        compResult *= -1;
                    }
                    return compResult;
                }
            }
            return 0;
        }

        public List<Integer> createIndexArray()
        {
            int numOfIndices = values.get(0).getLeft().size();
            List<Integer> indices = new ArrayList<>(values.get(0).getLeft().size());
            for (int i = 0; i < numOfIndices; i++)
            {
                indices.add(i);
            }
            return indices;
        }

        private final List<Pair<List<Property>, Boolean>> values;
        private final Comparator<Property> propertyComparator = new PropertyComparator();

    }

}
