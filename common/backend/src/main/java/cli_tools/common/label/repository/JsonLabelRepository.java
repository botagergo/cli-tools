package cli_tools.common.label.repository;

import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonLabelRepository extends SimpleJsonRepository<Map<String, List<String>>> implements LabelRepository {

    @Inject
    public JsonLabelRepository(@Named("labelJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public boolean create(String labelType, String labelText) throws IOException {
        List<String> labelTexts = getData().computeIfAbsent(labelType, k -> new ArrayList<>());
        if (!labelTexts.contains(labelText)) {
            labelTexts.add(labelText);
            writeData();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean exists(String labelType, String labelText) throws IOException {
        List<String> labelTexts = getData().get(labelType);
        if (labelTexts == null) {
            return false;
        }
        return labelTexts.contains(labelText);
    }

    @Override
    public List<String> getAll(String labelType) throws IOException {
        return getData().getOrDefault(labelType, List.of());
    }

    @Override
    public void deleteAll(String labelType) throws IOException {
        getData().remove(labelType);
        writeData();
    }

    @Override
    public Map<String, List<String>> getEmptyData() {
        return new HashMap<>();
    }
}
