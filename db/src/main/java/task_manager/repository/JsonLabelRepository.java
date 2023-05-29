package task_manager.repository;

import task_manager.data.Label;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonLabelRepository extends LabelRepository {

    public JsonLabelRepository(String labelName, File basePath) {
        super(labelName);
        this.basePath = basePath;
        this.jsonFile = new File(basePath, labelName + ".json");
    }

    @Override
    public Label find(String name) throws IOException {
        List<HashMap<String, Object>> labels = getLabels();

        Optional<HashMap<String, Object>> label =
                labels.stream().filter(t -> t.get("name").equals(name)).findAny();
        if (label.isEmpty()) {
            return null;
        }

        try {
            return new Label(UUID.fromString((String) label.get().get("uuid")), name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Label get(UUID uuid) throws IOException {
        List<HashMap<String, Object>> labels = getLabels();
        Optional<HashMap<String, Object>> label =
                labels.stream().filter(t -> t.get("uuid").equals(uuid.toString())).findAny();
        return label.map(stringObjectHashMap -> new Label(uuid, (String) stringObjectHashMap.get("name"))).orElse(null);
    }

    @Override
    public List<Label> getAll() throws IOException {
        List<HashMap<String, Object>> labels = getLabels();
        return labels.stream().map(label -> new Label(UUID.fromString((String) label.get("uuid")), (String) label.get("name"))).collect(Collectors.toList());
    }

    @Override
    public Label create(Label label) throws IOException {
        List<HashMap<String, Object>> labels = getLabels();
        labels.add(new HashMap<>(Map.of("uuid", label.uuid().toString(), "name", label.name())));
        writeLabels(labels);
        return label;
    }

    @Override
    public Label update(Label label) throws IOException {
        List<HashMap<String, Object>> labels = getLabels();
        Optional<HashMap<String, Object>> labelOptional =
                labels.stream().filter(t -> t.get("uuid").equals(label.uuid().toString())).findAny();
        if (labelOptional.isPresent()) {
            HashMap<String, Object> label_ = labelOptional.get();
            label_.put("name", label.name());
            writeLabels(labels);
            return new Label(UUID.fromString((String) label_.get("uuid")), (String) label_.get("name"));
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        List<HashMap<String, Object>> labels = getLabels();
        Optional<HashMap<String, Object>> label =
                labels.stream().filter(t -> t.get("uuid").equals(uuid.toString())).findAny();
        if (label.isPresent()) {
            labels.remove(label.get());
            JsonMapper.writeJson(jsonFile, labels);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteAll() throws IOException {
        JsonMapper.writeJson(jsonFile, List.of());
    }

    private List<HashMap<String, Object>> getLabels() throws IOException {
        if (labels == null) {
            labels = JsonMapper.readJson(jsonFile);
        }
        return labels;
    }

    private void writeLabels(List<HashMap<String, Object>> labels) throws IOException {
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }
        JsonMapper.writeJson(jsonFile, labels);
    }

    private List<HashMap<String, Object>> labels = null;
    private final File basePath;
    private final File jsonFile;

}
