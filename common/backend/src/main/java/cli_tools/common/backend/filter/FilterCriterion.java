package cli_tools.common.backend.filter;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.backend.property_comparator.PropertyComparator;
import cli_tools.common.backend.property_converter.PropertyConverter;
import cli_tools.common.property_lib.*;
import lombok.NonNull;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class FilterCriterion {
    public static FilterCriterion from(FilterPropertySpec filterPropertySpec) throws ServiceException {
        List<Object> operand = Objects.requireNonNullElse(filterPropertySpec.operand(), List.of());
        return create(filterPropertySpec.propertyDescriptor(), filterPropertySpec.predicate(), filterPropertySpec.negate(), operand);
    }

    public static FilterCriterion from(
            @NonNull FilterCriterionInfo filterCriterionInfo,
            @NonNull PropertyManager propertyManager,
            @NonNull PropertyConverter propertyConverter
    ) throws ServiceException {
        switch (filterCriterionInfo.type()) {
            case PROPERTY -> {
                PropertyDescriptor propertyDescriptor = propertyManager.getPropertyDescriptor(filterCriterionInfo.property());
                List<Object> operand = null;
                if (filterCriterionInfo.operands() != null) {
                    operand = propertyConverter.convertProperty(propertyDescriptor, filterCriterionInfo.operands());
                }

                Predicate predicate = filterCriterionInfo.predicate();
                boolean negate = false;
                if (predicate == null) {
                    predicate = filterCriterionInfo.predicateNegated();
                    negate = true;
                }

                return create(propertyDescriptor, predicate, negate, operand);
            }
            case AND -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(from(filterCriterionInfo_, propertyManager, propertyConverter));
                }
                return new AndFilterCriterion(filterCriteria);
            }
            case OR -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(from(filterCriterionInfo_, propertyManager, propertyConverter));
                }
                return new OrFilterCriterion(filterCriteria);
            }
            case NOT -> {
                return new NotFilterCriterion(from(
                        filterCriterionInfo.children().getFirst(), propertyManager, propertyConverter));
            }
            default ->
                    throw new NotImplementedException(filterCriterionInfo.type() + " filter criterion is not yet supported");
        }
    }

    private static FilterCriterion create(
            PropertyDescriptor propertyDescriptor, Predicate predicate, boolean negate, List<Object> operand
    ) throws ServiceException {
        String propertyName = propertyDescriptor.name();
        FilterCriterion filterCriterion = null;

        if (predicate == null) {
            if (operand.isEmpty()) {
                predicate = Predicate.TRUE;
            } else {
                predicate = Predicate.EQUALS;
            }
        }

        if (predicate.equals(Predicate.EQUALS)) {
            Object finalOperand;

            if (propertyDescriptor.isList()) {
                finalOperand = operand;
            } else if (propertyDescriptor.isSet()) {
                finalOperand = Set.copyOf(operand);
            } else if (operand.size() != 1) {
                throw new ServiceException("One argument expected for predicate '" + Predicate.EQUALS + "' of property '" + propertyName + "'", null);
            } else {
                finalOperand = operand.getFirst();
            }
            filterCriterion = new EqualFilterCriterion(propertyName, finalOperand);
        } else if (predicate == Predicate.LESS || predicate == Predicate.LESS_EQUAL ||
                predicate == Predicate.GREATER || predicate == Predicate.GREATER_EQUAL) {
            if (!PropertyComparator.isComparable(propertyDescriptor)) {
                throw new ServiceException("Predicate: " + predicate + " is incompatible with property '" + propertyName + "'");
            } else if (operand.size() != 1) {
                throw new ServiceException("One argument expected for predicate '" + predicate + "' of property '" + propertyName + "'");
            }
            Property property = Property.fromUnchecked(propertyDescriptor, operand.getFirst());
            switch (predicate) {
                case LESS ->
                        filterCriterion = new LessFilterCriterion(propertyName, property, new PropertyComparator(true));
                case LESS_EQUAL ->
                        filterCriterion = new LessEqualFilterCriterion(propertyName, property, new PropertyComparator(true));
                case GREATER ->
                        filterCriterion = new GreaterFilterCriterion(propertyName, property, new PropertyComparator(false));
                case GREATER_EQUAL ->
                        filterCriterion = new GreaterEqualFilterCriterion(propertyName, property, new PropertyComparator(false));
            }
        } else if (predicate == Predicate.TRUE || predicate == Predicate.FALSE || predicate == Predicate.FALSE_OR_NULL) {
            if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean || propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SINGLE) {
                throw new ServiceException("Predicate '" + predicate + "' is incompatible with property '" + propertyName + "'");
            }
            switch (predicate) {
                case TRUE -> filterCriterion = new EqualFilterCriterion(propertyName, true);
                case FALSE -> filterCriterion = new EqualFilterCriterion(propertyName, false);
                case FALSE_OR_NULL ->
                        filterCriterion = new NotFilterCriterion(new EqualFilterCriterion(propertyName, true));
            }
        } else {
            switch (predicate) {
                case CONTAINS -> {
                    if (propertyDescriptor.isCollection()) {
                        filterCriterion = new CollectionContainsFilterCriterion(propertyName, operand);
                    } else if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
                        if (operand.size() != 1) {
                            throw new ServiceException("One argument expected for predicate '" + Predicate.CONTAINS + "' of property '" + propertyName + "'");
                        }
                        filterCriterion = new ContainsCaseInsensitiveFilterCriterion(propertyName, (String) operand.getFirst());
                    } else {
                        throw new ServiceException("Predicate '" + Predicate.CONTAINS + "' is incompatible with property '" + propertyName + "'");
                    }
                }
                case IN -> filterCriterion = new InFilterCriterion(propertyName, operand);
                case NULL -> filterCriterion = new NullFilterCriterion(propertyName);
                case EMPTY -> filterCriterion = new EmptyFilterCriterion(propertyName);
            }
        }

        if (filterCriterion != null) {
            if (negate) {
                filterCriterion = new NotFilterCriterion(filterCriterion);
            }
            return filterCriterion;
        } else {
            throw new RuntimeException("Predicate '" + predicate + "' not implemented");
        }
    }

    public boolean check(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException {
        return check_(propertyOwner, propertyManager);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException;

}
