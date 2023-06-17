package task_manager.filter;

import lombok.Getter;
import task_manager.data.FilterCriterionInfo;
import task_manager.property.PropertyDescriptor;

public class FilterCriterionException extends Exception {

    public FilterCriterionException(Type exceptionType, PropertyDescriptor propertyDescriptor, FilterCriterionInfo.Predicate predicate) {
        super(getMsg(exceptionType, propertyDescriptor, predicate));

        this.exceptionType = exceptionType;
        this.propertyDescriptor = propertyDescriptor;
        this.predicate = null;
    }

    private static String getMsg(Type exceptionType, PropertyDescriptor propertyDescriptor, FilterCriterionInfo.Predicate predicate) {
        if (exceptionType == Type.InvalidTypeForPredicate) {
            return "Invalid type for predicate'" + predicate + "': " + propertyDescriptor.toString();
        }else {
            return null;
        }
    }

    @Getter final Type exceptionType;
    @Getter final PropertyDescriptor propertyDescriptor;
    @Getter final FilterCriterionInfo.Predicate predicate;

    public enum Type {
        InvalidTypeForPredicate
    }

}
