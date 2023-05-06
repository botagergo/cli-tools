package task_manager.repository;

import lombok.Getter;
import lombok.Setter;
import task_manager.data.Label;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public abstract class LabelRepository {

    public LabelRepository(String enumName) {
        this.enumName = enumName;
    }

    public abstract Label create(Label label) throws IOException;
    public abstract Label get(UUID uuid) throws IOException;
    public abstract List<Label> getAll() throws IOException;
    public abstract Label find(String name) throws IOException;
    public abstract Label update(Label label) throws IOException;
    public abstract boolean delete(UUID uuid) throws IOException;
    public abstract void deleteAll() throws IOException;

    @Getter
    @Setter
    private String enumName;

}
