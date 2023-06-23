package task_manager.core.property;

public record PropertyDescriptor(
        String name,
        PropertyDescriptor.Type type,
        Extra extra,
        Multiplicity multiplicity,
        Object defaultValue
) {

    public PropertyDescriptor {
        if (extra != null) {
            if (type == Type.UUID && !(extra instanceof UUIDExtra)) {
                throw new IllegalArgumentException("field 'extra' must have 'UUIDExtra' type for 'UUID'");
            }
        }
    }

    public boolean isCollection() {
        return multiplicity == Multiplicity.LIST || multiplicity == Multiplicity.SET;
    }

    public UUIDExtra getUuidExtraUnchecked() {
        if (extra == null) {
            return null;
        } else {
            return (UUIDExtra) extra;
        }
    }

    public IntegerExtra getIntegerExtraUnchecked() {
        if (extra == null) {
            return null;
        } else {
            return (IntegerExtra) extra;
        }
    }

    @Override
    public String toString() {
        return type.toString() + " " + multiplicity;
    }

    public enum Type {
        String, UUID, Boolean, Integer
    }


    public enum Multiplicity {
        SINGLE,
        LIST,
        SET
    }

    public interface Extra {
    }

    public record UUIDExtra(
            String labelName
    ) implements Extra {
    }

    public record IntegerExtra(
            String orderedLabelName
    ) implements Extra {
    }

}
