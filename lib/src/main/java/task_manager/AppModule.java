package task_manager;

import java.io.File;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import task_manager.db.task.TaskRepository;
import task_manager.db.property.JsonPropertyDescriptorRepository;
import task_manager.db.property.PropertyDescriptorRepository;
import task_manager.db.status.JsonStatusRepository;
import task_manager.db.status.StatusRepository;
import task_manager.db.tag.JsonTagRepository;
import task_manager.db.tag.TagRepository;
import task_manager.db.task.JsonTaskRepository;;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskRepository.class).to(JsonTaskRepository.class);
        bind(TagRepository.class).to(JsonTagRepository.class);
        bind(StatusRepository.class).to(JsonStatusRepository.class);
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class);
    }

    @Provides
    @Singleton
    JsonTaskRepository provideTaskRepository() {
        return new JsonTaskRepository(
            new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Provides
    @Singleton
    JsonTagRepository provideTagRepository() {
        return new JsonTagRepository(
            new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Provides
    @Singleton
    JsonStatusRepository provideStatusRepository() {
        return new JsonStatusRepository(
            new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Provides
    @Singleton
    JsonPropertyDescriptorRepository providePropertyDescriptorRepository() {
        return new JsonPropertyDescriptorRepository(
            new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }
}
