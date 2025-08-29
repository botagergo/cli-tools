package cli_tools.common.label.repository;

import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonLabelRepository extends SimpleJsonRepository<LinkedHashMap<String, List<String>>> implements LabelRepository {

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
    public List<String> getAllWithType(String labelType) throws IOException {
        return getData().getOrDefault(labelType, List.of());
    }

    @Override
    public Map<String, List<String>> getAll() throws IOException {
        return getData();
    }

    @Override
    public boolean delete(String labelType, String labelText) throws IOException {
        List<String> labelTexts = getData().get(labelType);
        if (labelTexts == null) {
            return false;
        }
        return labelTexts.remove(labelText);
    }

    @Override
    public void deleteAll(String labelType) throws IOException {
        getData().remove(labelType);
        writeData();
    }

    @Override
    public LinkedHashMap<String, List<String>> getEmptyData() {
        return new LinkedHashMap<>();
    }

    @Override
    protected TypeReference<LinkedHashMap<String, List<String>>> getTypeReference() {
        return new TypeReference<>() {
        };
    }

}
