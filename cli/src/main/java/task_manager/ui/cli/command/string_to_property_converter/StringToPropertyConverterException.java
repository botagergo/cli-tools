package task_manager.ui.cli.command.string_to_property_converter;

import lombok.Getter;

public class StringToPropertyConverterException extends Exception {

    public StringToPropertyConverterException(Type exceptionType, String msg, String argument) {
        super(msg);
        this.exceptionType = exceptionType;
        this.argument = argument;
    }

    @Getter final Type exceptionType;
    @Getter final String argument;

    public enum Type {
        NotAList,
        EmptyList,
        InvalidBoolean,
        LabelNotFound,
        OrderedLabelNotFound,
        InvalidPredicate,
        InvalidInteger,
        NoAssociatedLabel,
        InvalidPropertyOption,
        MissingPropertyValue
    }

}
