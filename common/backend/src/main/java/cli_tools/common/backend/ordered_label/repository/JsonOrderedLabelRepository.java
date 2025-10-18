package cli_tools.common.backend.ordered_label.repository;

import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.core.repository.OrderedLabelRepository;
import cli_tools.common.backend.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NonNull;

import java.io.File;
import java.util.*;

public class JsonOrderedLabelRepository
        extends SimpleJsonRepository<LinkedHashMap<String, TreeMap<Integer, String>>> implements OrderedLabelRepository {

    @Inject
    public JsonOrderedLabelRepository(@Named("orderedLabelJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public boolean create(@NonNull String type, @NonNull String text, int value) throws DataAccessException {
        TreeMap<Integer, String> orderedLabels = getData().computeIfAbsent(type, (_type) -> new TreeMap<>());
        if (orderedLabels.containsKey(value)) {
            throw new ConstraintViolationException("Ordered label with value %d already exists: %s"
                    .formatted(value, orderedLabels.get(value)));
        }

        orderedLabels.put(value, text);
        writeData();
        return true;
    }

    @Override
    public String get(@NonNull String type, int value) throws DataAccessException {
        var data = getData().get(type);
        if (data == null) {
            return null;
        }
        return data.get(value);
    }

    @Override
    public @NonNull List<String> getAll(@NonNull String type) throws DataAccessException {
        return getData().getOrDefault(type, new TreeMap<>()).values().stream().toList();
    }

    @Override
    public Integer find(@NonNull String type, @NonNull String text) throws DataAccessException {
        var labels = getData().get(type);
        if (labels == null) {
            return null;
        }
        return labels.entrySet().stream()
                .filter(entry_ -> entry_.getValue().equals(text))
                .findAny()
                .map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public void deleteAll(@NonNull String type) throws DataAccessException {
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
                typeFactory.constructMapType(TreeMap.class, Integer.class, String.class));
    }

    @Override
    public LinkedHashMap<String, TreeMap<Integer, String>> getEmptyData() {
        return new LinkedHashMap<>();
    }

}
