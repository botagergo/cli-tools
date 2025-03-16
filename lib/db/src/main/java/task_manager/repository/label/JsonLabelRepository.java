package task_manager.repository.label;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import task_manager.core.data.Label;
import task_manager.core.repository.LabelRepository;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonLabelRepository extends SimpleJsonRepository<ArrayList<Label>> implements LabelRepository {

    public JsonLabelRepository(File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(Label.class, LabelMixIn.class);
    }

    @Override
    public Label find(String labelText) throws IOException {
        return getData().stream().filter(t -> t.text().equals(labelText))
                .findAny().orElse(null);
    }

    @Override
    public Label get(UUID uuid) throws IOException {
        return getData().stream().filter(t -> t.uuid().equals(uuid))
                .findAny().orElse(null);
    }

    @Override
    public List<Label> getAll() throws IOException {
        return getData();
    }

    @Override
    public Label create(Label label) throws IOException {
        getData().add(label);
        writeData();
        return label;
    }

    @Override
    public void deleteAll() throws IOException {
        getData().clear();
        writeData();
    }

    @Override
    public ArrayList<Label> getEmptyData() {
        return new ArrayList<>();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Label.class);
    }
}
