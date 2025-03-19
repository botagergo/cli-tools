package cli_tools.common.filter;

import lombok.Getter;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.property_lib.PropertyDescriptor;

@Getter
public class FilterCriterionException extends Exception {

    final PropertyDescriptor propertyDescriptor;
    final Predicate predicate;

    final Type exceptionType;
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
