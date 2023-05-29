package task_manager.ui.cli;

import java.io.File;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.repository.*;
import task_manager.ui.cli.tokenizer.Tokenizer;
import task_manager.ui.cli.tokenizer.TokenizerImpl;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(TaskRepository.class).to(JsonTaskRepository.class);
        bind(LabelRepositoryFactory.class).to(JsonLabelRepositoryFactory.class);
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class);
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}
