package task_manager.core.data;

import lombok.With;

@With
public record OrderedLabel(String text, int value) {
}
