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
            if (type == Type.UUID && !(subtype instanceof Subtype.UUIDSubtype)) {
                throw new IllegalArgumentException("subtype must be UUIDSubtype for UUID");
            } else if (type == Type.String && !(subtype instanceof Subtype.StringSubtype)) {
                throw new IllegalArgumentException("subtype must be StringSubtype for String");
            } else if (type == Type.Integer && !(subtype instanceof Subtype.IntegerSubtype)) {
                throw new IllegalArgumentException("subtype must be IntegerSubtype for Integer");
            }
        }
    }

    public boolean isCollection() {
        return multiplicity == Multiplicity.LIST || multiplicity == Multiplicity.SET;
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
        return type.toString() + " " + multiplicity;
    }

    public enum Type {
        String, UUID, Boolean, Integer
    }

    public interface Subtype {
        String name();

        interface StringSubtype extends Subtype {}
        interface UUIDSubtype extends Subtype {}
        interface IntegerSubtype extends Subtype {}
        record LabelSubtype(String labelType) implements UUIDSubtype {
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
        record DateSubtype() implements StringSubtype {
            @Override
            public String name() {
                return "Date";
            }
        }
        record TimeSubtype() implements StringSubtype {
            @Override
            public String name() {
                return "Time";
            }
        }
        record TaskSubtype() implements UUIDSubtype {
            @Override
            public String name() {
                return "Task";
            }
        }
    }

    public enum Multiplicity {
        SINGLE,
        LIST,
        SET
    }

}
