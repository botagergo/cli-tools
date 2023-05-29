package task_manager.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import task_manager.data.Label;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @Getter @Setter
public abstract class LabelRepository {

    public abstract Label create(Label label) throws IOException;
    public abstract Label get(UUID uuid) throws IOException;
    public abstract List<Label> getAll() throws IOException;
    public abstract Label find(String name) throws IOException;
    public abstract Label update(Label label) throws IOException;
    public abstract boolean delete(UUID uuid) throws IOException;
    public abstract void deleteAll() throws IOException;

    private String enumName;

}
