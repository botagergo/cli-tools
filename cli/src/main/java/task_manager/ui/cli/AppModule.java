package task_manager.ui.cli;

import java.io.File;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.logic.use_case.*;
import task_manager.repository.*;
import task_manager.ui.cli.tokenizer.Tokenizer;
import task_manager.ui.cli.tokenizer.TokenizerImpl;
import task_manager.util.RandomUUIDGenerator;
import task_manager.util.UUIDGenerator;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(TaskRepository.class).to(JsonTaskRepository.class);
        bind(LabelRepositoryFactory.class).to(JsonLabelRepositoryFactory.class);
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class);
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class);
        bind(TaskUseCase.class).to(TaskUseCaseImpl.class);
        bind(TagUseCase.class).to(TagUseCaseImpl.class);
        bind(StatusUseCase.class).to(StatusUseCaseImpl.class);
        bind(PropertyDescriptorUseCase.class).to(PropertyDescriptorUseCaseImpl.class);
        bind(TempIDMappingRepository.class).to(JsonTempIDMappingRepository.class);
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}
