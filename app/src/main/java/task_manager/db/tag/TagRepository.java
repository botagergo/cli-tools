package task_manager.db.tag;

import java.io.IOException;
import java.util.UUID;

public interface TagRepository {
    public Tag findTag(String name) throws IOException;

    public Tag getTag(UUID uuid) throws IOException;

    public Tag addTag(String name) throws IOException;

    public void deleteAllTags() throws IOException;
}
