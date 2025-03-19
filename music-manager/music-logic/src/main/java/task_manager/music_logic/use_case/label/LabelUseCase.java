package common.music_logic.use_case.label;

import common.core.data.Label;

import java.io.IOException;
import java.util.UUID;

public interface LabelUseCase {
    Label getLabel(String labelType, UUID uuid) throws IOException;

    Label findLabel(String labelType, String labelText) throws IOException;

    Label createLabel(String labelType, String labelText) throws IOException;

    Label createLabel(String labelName, Label label) throws IOException;

    void deleteAllLabels(String labelName) throws IOException;
}
