package task_manager.logic.use_case.label;

import task_manager.data.Label;

import java.io.IOException;
import java.util.UUID;

public interface LabelUseCase {
    Label getLabel(String labelType, UUID uuid) throws IOException;

    Label createLabel(String labelType, String labelText) throws IOException;
}
