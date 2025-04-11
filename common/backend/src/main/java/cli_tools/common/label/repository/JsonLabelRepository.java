package cli_tools.common.label.repository;

import cli_tools.common.core.data.Label;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonLabelRepository extends SimpleJsonRepository<ArrayList<Label>> implements LabelRepository {

    public JsonLabelRepository(File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(Label.class, LabelMixIn.class);
    }

    @Override
    public Label create(Label label) throws IOException {
        getData().add(label);
        writeData();
        return label;
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
    public Label find(String labelText) throws IOException {
        return getData().stream().filter(t -> t.text().equals(labelText))
                .findAny().orElse(null);
    }

    @Override
    public void deleteAll() throws IOException {
        getData().clear();
        writeData();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Label.class);
    }

    @Override
    public ArrayList<Label> getEmptyData() {
        return new ArrayList<>();
    }
}
