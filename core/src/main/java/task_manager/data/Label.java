package task_manager.data;

import lombok.With;

import java.util.UUID;

@With
public record Label(UUID uuid, String name)
{}
