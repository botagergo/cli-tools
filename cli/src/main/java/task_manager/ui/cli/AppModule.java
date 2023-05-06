package task_manager.ui.cli;

import java.io.File;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.annotation.StatusAnnotation;
import task_manager.annotation.TagAnnotation;
import task_manager.repository.*;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TaskRepository.class).to(JsonTaskRepository.class);
        bind(LabelRepository.class).annotatedWith(StatusAnnotation.class).to(JsonStatusRepository.class);
        bind(LabelRepository.class).annotatedWith(TagAnnotation.class).to(JsonTagRepository.class);
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class);
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}
