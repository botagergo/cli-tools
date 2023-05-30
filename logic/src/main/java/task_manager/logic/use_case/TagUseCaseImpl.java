package task_manager.logic.use_case;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.data.Tag;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.util.UUIDGenerator;

public class TagUseCaseImpl implements TagUseCase {

    @Inject
    public TagUseCaseImpl(LabelRepositoryFactory labelRepositoryFactory, UUIDGenerator uuidGenerator) {
        this.labelRepository = labelRepositoryFactory.getLabelRepository("tags");
        this.uuidGenerator = uuidGenerator;
    }

    public Tag addTag(String name) throws IOException {
        return Tag.fromLabel(labelRepository.create(new Label(uuidGenerator.getUUID(), name)));
    }

    public Tag findTag(String tagName) throws IOException {
        return Tag.fromLabel(labelRepository.find(tagName));
    }

    public Tag getTag(UUID uuid) throws IOException {
        return Tag.fromLabel(labelRepository.get(uuid));
    }

    public List<Tag> getTags() throws IOException {
        return labelRepository.getAll().stream().map(Tag::fromLabel).collect(Collectors.toList());
    }

    public Tag update(Tag tag) throws IOException {
        return Tag.fromLabel(labelRepository.update(tag.asLabel()));
    }

    public boolean delete(UUID uuid) throws IOException {
        return labelRepository.delete(uuid);
    }

    public void deleteAllTags() throws IOException {
        labelRepository.deleteAll();
    }

    private final LabelRepository labelRepository;
    private final UUIDGenerator uuidGenerator;

}
