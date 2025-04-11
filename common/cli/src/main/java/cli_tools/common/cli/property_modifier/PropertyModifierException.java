package cli_tools.common.cli.property_modifier;

import cli_tools.common.core.data.property.ModifyPropertySpec;
import lombok.Getter;

@Getter
public class PropertyModifierException extends Exception {

    private final Type type;
    private final ModifyPropertySpec modifyPropertySpec;
    public PropertyModifierException(Type type, ModifyPropertySpec modifyPropertySpec, String msg) {
        super(msg);
        this.type = type;
        this.modifyPropertySpec = modifyPropertySpec;
    }

    public enum Type {
        BadModificationType, NotACollection
    }

}
