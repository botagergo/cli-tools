package common.music_cli.command.property_modifier;

import lombok.Getter;
import common.core.property.ModifyPropertySpec;

@Getter
public class PropertyModifierException extends Exception {

    public PropertyModifierException(Type type, ModifyPropertySpec modifyPropertySpec, String msg) {
        super(msg);
        this.type = type;
        this.modifyPropertySpec = modifyPropertySpec;
    }

    private final Type type;
    private final ModifyPropertySpec modifyPropertySpec;

    public enum Type {
        BadModificationType, NotACollection
    }

}
