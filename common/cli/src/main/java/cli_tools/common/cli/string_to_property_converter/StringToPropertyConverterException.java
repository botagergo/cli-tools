package cli_tools.common.cli.string_to_property_converter;

import lombok.Getter;

@Getter
public class StringToPropertyConverterException extends Exception {

    final Type exceptionType;
    final String argument;
    public StringToPropertyConverterException(Type exceptionType, String msg, String argument) {
        super(msg);
        this.exceptionType = exceptionType;
        this.argument = argument;
    }

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
        UnexpectedPropertyValue,
        InvalidDate,
        InvalidTime,
        InvalidTempId,
        TempIdNotFound,
        InvalidUuid
    }

}
