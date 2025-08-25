package cli_tools.common.label.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface LabelService {
    boolean labelExists(String labelType, String labelText) throws IOException;

    List<String> getLabels(String labelType) throws IOException;

    boolean createLabel(String labelType, String labelText) throws IOException;

    void deleteAllLabels(String labelType) throws IOException;
}
