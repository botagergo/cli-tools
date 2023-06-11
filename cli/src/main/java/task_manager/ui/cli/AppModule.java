package task_manager.ui.cli;

import java.io.File;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.logic.use_case.*;
import task_manager.repository.*;
import task_manager.ui.cli.command_line.CommandLine;
import task_manager.ui.cli.command_line.Executor;
import task_manager.ui.cli.command_line.ExecutorImpl;
import task_manager.ui.cli.command_line.JlineCommandLine;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
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
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class);
        bind(CommandLine.class).to(JlineCommandLine.class);
        bind(Executor.class).to(ExecutorImpl.class);
        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/task.json"));
        bind(File.class).annotatedWith(Names.named("tempIdMappingJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/temp_id_mapping.json"));
        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}
