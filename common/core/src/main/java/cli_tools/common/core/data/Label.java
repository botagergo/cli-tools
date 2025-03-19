package cli_tools.common.core.data;

import lombok.With;

import java.util.UUID;

@With
public record Label(UUID uuid, String text) {
}
