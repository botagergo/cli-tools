package cli_tools.common.backend.label.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface LabelService {
    boolean labelExists(String labelType, String labelText) throws IOException;

    List<String> getLabels(String labelType) throws IOException;

    Map<String, List<String>> getAllLabels() throws IOException;

    boolean createLabel(String labelType, String labelText) throws IOException;

    boolean deleteLabel(String labelType, String labelText) throws IOException;

    void deleteAllLabels(String labelType) throws IOException;
}
