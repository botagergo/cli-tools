package common.music_cli;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import common.cli.tokenizer.Tokenizer;
import common.cli.tokenizer.TokenizerImpl;
import common.music_logic.use_case.label.LabelUseCase;
import common.music_logic.use_case.label.LabelUseCaseImpl;
import common.music_logic.use_case.ordered_label.OrderedLabelUseCase;
import common.music_logic.use_case.ordered_label.OrderedLabelUseCaseImpl;
import common.music_logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import common.music_logic.use_case.property_descriptor.PropertyDescriptorUseCaseImpl;
import common.music_logic.use_case.task.TaskUseCase;
import common.music_logic.use_case.task.TaskUseCaseImpl;
import common.music_logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import common.music_logic.use_case.temp_id_mapping.TempIDMappingUseCaseImpl;
import common.music_logic.use_case.view.ViewInfoUseCase;
import common.music_logic.use_case.view.ViewInfoUseCaseImpl;
import common.property_lib.PropertyManager;
import common.music_cli.command_parser.CommandParserFactory;
import common.music_cli.command_parser.CommandParserFactoryImpl;
import common.repository.ConfigurationRepositoryImpl;
import common.repository.JsonStateRepository;
import common.repository.label.JsonLabelRepositoryFactory;
import common.repository.ordered_label.JsonOrderedLabelRepositoryFactory;
import common.repository.property_descriptor.JsonPropertyDescriptorRepository;
import common.repository.task.JsonTaskRepository;
import common.repository.temp_id_mapping.JsonTempIDMappingRepository;
import common.repository.view.JsonViewInfoRepository;
import common.music_cli.command_line.CommandLine;
import common.music_cli.command_line.Executor;
import common.music_cli.command_line.ExecutorImpl;
import common.music_cli.command_line.JlineCommandLine;
import common.util.RandomUUIDGenerator;
import common.util.UUIDGenerator;

import java.io.File;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        String basePath = System.getenv("TASK_MANAGER_BASE_PATH");
        if (basePath == null) {
            basePath = System.getProperty("user.home") + "/.config/common/";
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
        bind(File.class).annotatedWith(Names.named("viewJsonFile")).toInstance(new File(basePath + "view.json"));
        bind(File.class).annotatedWith(Names.named("stateJsonFile")).toInstance(new File(basePath + "state.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(new File(basePath + "config.yaml"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(basePath));
    }

}
