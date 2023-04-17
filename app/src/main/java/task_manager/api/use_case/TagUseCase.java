package task_manager.api.use_case;

import java.io.IOException;
import java.util.UUID;
import com.google.inject.Inject;
import task_manager.db.tag.Tag;
import task_manager.db.tag.TagRepository;

public class TagUseCase {

    public Tag addTag(String tagName) throws IOException {
        return tagRepository.addTag(tagName);
    }

    public Tag findTag(String tagName) throws IOException {
        return tagRepository.findTag(tagName);
    }

    public Tag getTag(UUID uuid) throws IOException {
        return tagRepository.getTag(uuid);
    }

    public void deleteAllTags() throws IOException {
        tagRepository.deleteAllTags();
    }

    @Inject
    private TagRepository tagRepository;

}
