package task_manager.logic.use_case.tag;

import task_manager.data.Tag;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TagUseCase {

    Tag addTag(String name) throws IOException;

    Tag findTag(String tagName) throws IOException;

    Tag getTag(UUID uuid) throws IOException;

    List<Tag> getTags() throws IOException;

    Tag update(Tag tag) throws IOException;

    boolean delete(UUID uuid) throws IOException;

    void deleteAllTags() throws IOException;

}
