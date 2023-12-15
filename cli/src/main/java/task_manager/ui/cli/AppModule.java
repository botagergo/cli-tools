package task_manager.ui.cli;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.core.repository.*;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.label.LabelUseCaseImpl;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCaseImpl;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCaseImpl;
import task_manager.logic.use_case.task.TaskUseCase;
import task_manager.logic.use_case.task.TaskUseCaseImpl;
import task_manager.logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import task_manager.logic.use_case.temp_id_mapping.TempIDMappingUseCaseImpl;
import task_manager.logic.use_case.view.ViewInfoUseCase;
import task_manager.logic.use_case.view.ViewInfoUseCaseImpl;
import task_manager.property_lib.PropertyManager;
import task_manager.repository.ConfigurationRepositoryImpl;
import task_manager.repository.JsonStateRepository;
import task_manager.repository.label.JsonLabelRepositoryFactory;
import task_manager.repository.ordered_label.JsonOrderedLabelRepositoryFactory;
import task_manager.repository.property_descriptor.JsonPropertyDescriptorRepository;
import task_manager.repository.task.JsonTaskRepository;
import task_manager.repository.temp_id_mapping.JsonTempIDMappingRepository;
import task_manager.repository.view.JsonViewInfoRepository;
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

import java.io.File;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        String basePath = System.getenv("TASK_MANAGER_BASE_PATH");
        if (basePath == null) {
            basePath = System.getProperty("user.home") + "/.config/task_manager/";
        }

        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(TaskRepository.class).to(JsonTaskRepository.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(LabelRepositoryFactory.class).to(JsonLabelRepositoryFactory.class).asEagerSingleton();
        bind(OrderedLabelRepositoryFactory.class).to(JsonOrderedLabelRepositoryFactory.class).asEagerSingleton();
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(ConfigurationRepositoryImpl.class).asEagerSingleton();
        bind(StateRepository.class).to(JsonStateRepository.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class).asEagerSingleton();
        bind(TaskUseCase.class).to(TaskUseCaseImpl.class).asEagerSingleton();
        bind(LabelUseCase.class).to(LabelUseCaseImpl.class).asEagerSingleton();
        bind(OrderedLabelUseCase.class).to(OrderedLabelUseCaseImpl.class).asEagerSingleton();
        bind(ViewInfoUseCase.class).to(ViewInfoUseCaseImpl.class).asEagerSingleton();
        bind(PropertyDescriptorUseCase.class).to(PropertyDescriptorUseCaseImpl.class).asEagerSingleton();
        bind(TempIDMappingUseCase.class).to(TempIDMappingUseCaseImpl.class).asEagerSingleton();
        bind(TempIDMappingRepository.class).to(JsonTempIDMappingRepository.class).asEagerSingleton();
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(CommandLine.class).to(JlineCommandLine.class);
        bind(Executor.class).to(ExecutorImpl.class);
        bind(LabelUseCase.class).to(LabelUseCaseImpl.class).asEagerSingleton();

        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(new File(basePath + "task.json"));
        bind(File.class).annotatedWith(Names.named("tempIdMappingJsonFile")).toInstance(new File(basePath + "temp_id_mapping.json"));
        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(new File(basePath + "property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("viewInfoJsonFile")).toInstance(new File(basePath + "view_info.json"));
        bind(File.class).annotatedWith(Names.named("stateJsonFile")).toInstance(new File(basePath + "state.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(new File(basePath + "config.yaml"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(basePath));
    }

}
