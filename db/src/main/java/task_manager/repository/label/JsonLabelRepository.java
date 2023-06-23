package task_manager.repository.label;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import task_manager.data.Label;
import task_manager.repository.LabelRepository;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class JsonLabelRepository extends SimpleJsonRepository<ArrayList<Label>> implements LabelRepository {

    public JsonLabelRepository(File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(Label.class, LabelMixIn.class);
    }

    @Override
    public Label find(String name) throws IOException {
        return getData().stream().filter(t -> t.name().equals(name))
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
    public Label update(Label label) throws IOException {
        ArrayList<Label> labels = getData();
        OptionalInt indexOptional = IntStream.range(0, labels.size()).filter(i -> labels.get(i).uuid().equals(label.uuid())).findAny();
        if (indexOptional.isPresent()) {
            int index = indexOptional.getAsInt();
            labels.set(index, labels.get(index).withName(label.name()));
            writeData();
            return labels.get(index);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        ArrayList<Label> labels = getData();
        Optional<Label> label =
                labels.stream().filter(t -> t.uuid().equals(uuid)).findAny();
        if (label.isPresent()) {
            labels.remove(label.get());
            writeData();
            return true;
        } else {
            return false;
        }
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
