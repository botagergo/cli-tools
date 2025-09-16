package cli_tools.common.backend.ordered_label.repository;

import cli_tools.common.core.repository.OrderedLabelRepository;
import cli_tools.common.backend.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JsonOrderedLabelRepository
        extends SimpleJsonRepository<LinkedHashMap<String, List<String>>> implements OrderedLabelRepository {

    @Inject
    public JsonOrderedLabelRepository(@Named("orderedLabelJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public void create(String type, String text) throws IOException {
        getData().computeIfAbsent(type, (_type) -> new ArrayList<>()).add(text);
        writeData();
    }

    @Override
    public String get(String type, int value) throws IOException {
        var data = getData().get(type);
        if (data == null || value < 0 || value >= data.size()) {
            return null;
        }
        return data.get(value);
    }

    @Override
    public List<String> getAll(String type) throws IOException {
        return getData().getOrDefault(type, List.of());
    }

    @Override
    public Integer find(String type, String text) throws IOException {
        var labels = getData().get(type);
        if (labels == null) {
            return null;
        }
        int index = labels.indexOf(text);
        if (index == -1) {
            return null;
        }
        return index;
    }

    @Override
    public void deleteAll(String type) throws IOException {
        var labels = getData().get(type);
        if (labels != null) {
            labels.clear();
            writeData();
        }
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(
                LinkedHashMap.class,
                typeFactory.constructType(String.class),
                typeFactory.constructCollectionType(List.class, String.class));
    }

    @Override
    public LinkedHashMap<String, List<String>> getEmptyData() {
        return new LinkedHashMap<>();
    }

}
