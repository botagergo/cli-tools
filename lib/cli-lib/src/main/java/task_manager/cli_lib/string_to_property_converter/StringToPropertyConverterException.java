package task_manager.cli_lib.string_to_property_converter;

import lombok.Getter;

@Getter
public class StringToPropertyConverterException extends Exception {

    public StringToPropertyConverterException(Type exceptionType, String msg, String argument) {
        super(msg);
        this.exceptionType = exceptionType;
        this.argument = argument;
    }

    final Type exceptionType;
    final String argument;

    public enum Type {
        NotAList,
        EmptyList,
        InvalidBoolean,
        LabelNotFound,
        OrderedLabelNotFound,
        InvalidPredicate,
        NoAssociatedLabel,
        InvalidPropertyOption,
        MissingPropertyValue,
        InvalidDate,
        InvalidTime
    }

}
