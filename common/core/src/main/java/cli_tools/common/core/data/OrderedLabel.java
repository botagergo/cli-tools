package cli_tools.common.core.data;

import lombok.With;

@With
public record OrderedLabel(String text, int value) {
}
