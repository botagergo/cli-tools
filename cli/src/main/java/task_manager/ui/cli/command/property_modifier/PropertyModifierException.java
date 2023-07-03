package task_manager.ui.cli.command.property_modifier;

import lombok.Getter;
import task_manager.core.property.ModifyPropertySpec;

public class PropertyModifierException extends Exception {

    public PropertyModifierException(Type type, ModifyPropertySpec modifyPropertySpec, String msg) {
        super(msg);
        this.type = type;
        this.modifyPropertySpec = modifyPropertySpec;
    }

    @Getter private final Type type;
    @Getter private final ModifyPropertySpec modifyPropertySpec;

    public enum Type {
        BadModificationType, NotACollection
    }

}
