package task_manager.ui.cli.command.property_modifier;

public class PropertyModifierException extends Exception {

    public PropertyModifierException() {
        super("Predicate not allowed when setting/modifying property");
    }

}
