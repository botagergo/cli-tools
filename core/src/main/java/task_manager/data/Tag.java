package task_manager.data;

import java.util.UUID;

public record Tag(UUID uuid, String name) {

    public static Tag fromLabel(Label label) {
        if (label == null) {
            return null;
        }
        return new Tag(label.uuid(), label.name());
    }

    public Label asLabel() {
        return new Label(uuid, name);
    }

}
