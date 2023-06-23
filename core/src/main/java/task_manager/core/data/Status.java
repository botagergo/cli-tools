package task_manager.core.data;

import java.util.UUID;

public record Status(UUID uuid, String name) {

    public static Status fromLabel(Label label) {
        if (label == null) {
            return null;
        }
        return new Status(label.uuid(), label.text());
    }

}
