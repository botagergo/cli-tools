package cli_tools.common.label.service;

import cli_tools.common.core.data.Label;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface LabelService {
    Label getLabel(String labelType, UUID uuid) throws IOException;
    List<Label> getLabels(String labelType) throws IOException;
    Label findLabel(String labelType, String labelText) throws IOException;
    Label createLabel(String labelType, String labelText) throws IOException;
    Label createLabel(String labelType, Label label) throws IOException;
    void deleteAllLabels(String labelType) throws IOException;
}
