package cli_tools.common.property_lib;

import lombok.NonNull;

public record PropertyDescriptor(
        @NonNull String name,
        @NonNull Type type,
        Subtype subtype,
        @NonNull Multiplicity multiplicity,
        Object defaultValue,
        PseudoPropertyProvider pseudoPropertyProvider
) {

    public PropertyDescriptor {
        if (subtype != null) {
            switch (type) {
                case String -> {
                    if (!(subtype instanceof Subtype.StringSubtype)) {
                        throw new IllegalArgumentException("subtype must be StringSubtype for String");
                    }
                }
                case UUID -> {
                    if (!(subtype instanceof Subtype.UUIDSubtype)) {
                        throw new IllegalArgumentException("subtype must be UUIDSubtype for UUID");
                    }
                }
                case Integer -> {
                    if (!(subtype instanceof Subtype.IntegerSubtype)) {
                        throw new IllegalArgumentException("subtype must be IntegerSubtype for Integer");
                    }
                }
                case Boolean -> throw new IllegalArgumentException("no subtype allowed for Boolean");
                case Date -> throw new IllegalArgumentException("no subtype allowed for Date");
                case Time -> throw new IllegalArgumentException("no subtype allowed for Time");
            }
        }
    }

    public boolean isCollection() {
        return multiplicity == Multiplicity.LIST || multiplicity == Multiplicity.SET;
    }

    public boolean isList() {
        return multiplicity == Multiplicity.LIST;
    }

    public boolean isSet() {
        return multiplicity == Multiplicity.SET;
    }

    public Subtype.UUIDSubtype getUuidSubtypeUnchecked() {
        if (subtype == null) {
            return null;
        } else {
            return (Subtype.UUIDSubtype) subtype;
        }
    }

    public Subtype.IntegerSubtype getIntegerSubtypeUnchecked() {
        if (subtype == null) {
            return null;
        } else {
            return (Subtype.IntegerSubtype) subtype;
        }
    }

    @Override
    public String toString() {
        return type + " " + multiplicity;
    }

    public enum Type {
        String, UUID, Boolean, Integer, Date, Time
    }

    public enum Multiplicity {
        SINGLE,
        LIST,
        SET
    }

    public sealed interface Subtype {
        String name();

        sealed interface StringSubtype extends Subtype { }

        sealed interface UUIDSubtype extends Subtype { }

        sealed interface IntegerSubtype extends Subtype { }

        record LabelSubtype(String labelType) implements StringSubtype {
            @Override
            public String name() {
                return "Label";
            }
        }

        record OrderedLabelSubtype(String orderedLabelType) implements IntegerSubtype {
            @Override
            public String name() {
                return "OrderedLabel";
            }
        }

        record TaskSubtype() implements UUIDSubtype {
            @Override
            public String name() {
                return "Task";
            }
        }
    }

}
