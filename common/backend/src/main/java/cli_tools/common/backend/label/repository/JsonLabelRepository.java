package cli_tools.common.backend.label.repository;

import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.backend.repository.SimpleJsonRepository;
import cli_tools.common.util.UUIDGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.util.*;

@Getter
public class JsonLabelRepository extends SimpleJsonRepository<LinkedHashMap<String, LinkedHashMap<UUID, String>>> implements LabelRepository {

    private final UUIDGenerator uuidGenerator;

    @Inject
    public JsonLabelRepository(UUIDGenerator uuidGenerator, @Named("labelJsonFile") File jsonFile) {
        super(jsonFile);
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public @NonNull UUID create(@NonNull String labelType, @NonNull String labelText) throws DataAccessException {
        LinkedHashMap<UUID, String> labelTexts = getData().computeIfAbsent(labelType, k -> new LinkedHashMap<>());
        if (!labelTexts.containsValue(labelText)) {
            UUID uuid = uuidGenerator.getUUID();
            labelTexts.put(uuid, labelText);
            writeData();
            return uuid;
        } else {
            throw new ConstraintViolationException("Label '%s' (%s) already exists".formatted(labelText, labelType), null);
        }
    }

    @Override
    public String get(@NonNull String labelType, @NonNull UUID uuid) throws DataAccessException {
        LinkedHashMap<UUID, String> labelTexts = getData().get(labelType);
        if (labelTexts == null) {
            return null;
        }
        return labelTexts.get(uuid);
    }

    @Override
    public UUID find(@NonNull String labelType, @NonNull String labelText) throws DataAccessException {
        LinkedHashMap<UUID, String> labelTexts = getData().get(labelType);
        if (labelTexts == null) {
            return null;
        }
        var entry = labelTexts.entrySet().stream().filter(entry_ -> entry_.getValue().equals(labelText))
                .findFirst();
        return entry.map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public @NonNull List<String> getAllWithType(@NonNull String labelType) throws DataAccessException {
        return getData().getOrDefault(labelType, new LinkedHashMap<>())
                .values().stream().toList();
    }

    @Override
    public @NonNull LinkedHashMap<String, LinkedHashMap<UUID, String>> getAll() throws DataAccessException {
        return getData();
    }

    @Override
    public boolean delete(@NonNull String labelType, @NonNull UUID uuid) throws DataAccessException {
        LinkedHashMap<UUID, String> labelTexts = getData().get(labelType);
        if (labelTexts == null) {
            return false;
        }
        return labelTexts.remove(uuid) != null;
    }

    @Override
    public void deleteAll(@NonNull String labelType) throws DataAccessException {
        getData().remove(labelType);
        writeData();
    }

    @Override
    public LinkedHashMap<String, LinkedHashMap<UUID, String>> getEmptyData() {
        return new LinkedHashMap<>();
    }

    @Override
    protected TypeReference<LinkedHashMap<String, LinkedHashMap<UUID, String>>> getTypeReference() {
        return new TypeReference<>() {
        };
    }

}
