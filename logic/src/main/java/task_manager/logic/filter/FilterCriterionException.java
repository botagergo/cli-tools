package task_manager.logic.filter;

import lombok.Getter;
import task_manager.core.data.Predicate;
import task_manager.core.property.PropertyDescriptor;

public class FilterCriterionException extends Exception {

    @Getter
    final PropertyDescriptor propertyDescriptor;
    @Getter
    final Predicate predicate;

    @Getter final Type exceptionType;
    public FilterCriterionException(Type exceptionType, PropertyDescriptor propertyDescriptor, Predicate predicate) {
        super(getMsg(exceptionType, propertyDescriptor, predicate));

        this.exceptionType = exceptionType;
        this.propertyDescriptor = propertyDescriptor;
        this.predicate = null;
    }

    private static String getMsg(Type exceptionType, PropertyDescriptor propertyDescriptor, Predicate predicate) {
        if (exceptionType == Type.InvalidTypeForPredicate) {
            return "Invalid type for predicate'" + predicate + "': " + propertyDescriptor.toString();
        } else {
            return null;
        }
    }

    public enum Type {
        InvalidTypeForPredicate
    }

}
