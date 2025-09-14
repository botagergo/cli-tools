package cli_tools.common.ordered_label.repository;

import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.OrderedLabelRepository;
import cli_tools.common.repository.JsonRepository;
import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonOrderedLabelRepository
        extends SimpleJsonRepository<LinkedHashMap<String, List<String>>> implements OrderedLabelRepository {

    @Inject
    public JsonOrderedLabelRepository(@Named("orderedLabelJsonFile") File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(OrderedLabelMixIn.class, OrderedLabelMixIn.class);
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
        return labels.indexOf(text);
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
        return typeFactory.constructCollectionType(ArrayList.class, String.class);
    }

    @Override
    public LinkedHashMap<String, List<String>> getEmptyData() {
        return new LinkedHashMap<>();
    }

}
