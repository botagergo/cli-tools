package task_manager.logic.use_case.view;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.NotImplementedException;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.Task;
import task_manager.core.data.ViewInfo;
import task_manager.core.property.Property;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.repository.ViewInfoRepository;
import task_manager.logic.PropertyComparator;
import task_manager.logic.filter.*;
import task_manager.logic.sorter.PropertySorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class ViewUseCaseImpl implements ViewUseCase {

    @Override
    public View getView(String name, PropertyManager propertyManager)
            throws IOException, PropertyException, PropertyConverterException, FilterCriterionException {
        ViewInfo viewInfo = viewInfoRepository.get(name);

        if (viewInfo == null) {
            return null;
        }

        PropertySorter<Task> propertySorter = null;
        FilterCriterion filterCriterion = null;

        if (viewInfo.sortingInfo() != null) {
            propertySorter = new PropertySorter<>(viewInfo.sortingInfo().sortingCriteria());
        }

        if (viewInfo.filterCriterionInfo() != null) {
            filterCriterion = createFilterCriterion(viewInfo.filterCriterionInfo(), propertyManager);
        }

        return new View(name, propertySorter, filterCriterion);
    }

    private FilterCriterion createFilterCriterion(@NonNull FilterCriterionInfo filterCriterionInfo, @NonNull PropertyManager propertyManager) throws PropertyException, IOException, PropertyConverterException, FilterCriterionException {
        switch (filterCriterionInfo.type()) {
            case PROPERTY -> {
                return createPropertyFilterCriterion(filterCriterionInfo, propertyManager);
            }
            case AND -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(createFilterCriterion(filterCriterionInfo_, propertyManager));
                }
                return new AndFilterCriterion(filterCriteria);
            }
            case OR -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(createFilterCriterion(filterCriterionInfo_, propertyManager));
                }
                return new OrFilterCriterion(filterCriteria);
            }
            case NOT -> {
                return new NotFilterCriterion(createFilterCriterion(
                        filterCriterionInfo.children().get(0), propertyManager));
            }
            default -> throw new NotImplementedException(filterCriterionInfo.type() + " filter criterion is not yet supported");
        }
    }

    @SuppressWarnings("unchecked")
    private FilterCriterion createPropertyFilterCriterion(FilterCriterionInfo filterCriterionInfo, PropertyManager propertyManager) throws PropertyException, IOException, PropertyConverterException, FilterCriterionException {
        PropertyDescriptor propertyDescriptor = propertyManager.getPropertyDescriptor(filterCriterionInfo.propertyName());
        Object operand = propertyConverter.convertProperty(propertyDescriptor, filterCriterionInfo.operands());

        switch (filterCriterionInfo.predicate()) {
            case EQUALS -> {
                return new EqualFilterCriterion(filterCriterionInfo.propertyName(), operand);
            }
            case CONTAINS -> {
                if (propertyDescriptor.isCollection()) {
                    return new CollectionContainsFilterCriterion(filterCriterionInfo.propertyName(), (Collection<Object>) operand);
                } else if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
                    return new ContainsCaseInsensitiveFilterCriterion(filterCriterionInfo.propertyName(), (String) operand);
                } else {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                }
            }
            case LESS -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new LessFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case LESS_EQUAL -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new LessEqualFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case GREATER -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new GreaterFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case GREATER_EQUAL -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new GreaterEqualFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            default ->
                    throw new NotImplementedException(filterCriterionInfo.type() + " predicate is not yet supported");
        }
    }

    private final ViewInfoRepository viewInfoRepository;
    private final PropertyConverter propertyConverter;

}
