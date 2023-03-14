package task_manager.api.use_case;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import task_manager.db.tag.JsonTagRepository;
import task_manager.db.tag.Tag;
import task_manager.db.tag.TagRepository;

public class TagUseCase {

    public TagUseCase() {
        this.tagRepository = new JsonTagRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

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

    private TagRepository tagRepository;

}
