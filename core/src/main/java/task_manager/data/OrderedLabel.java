package task_manager.data;

import lombok.With;

@With
public record OrderedLabel(String text, int value) {
}
