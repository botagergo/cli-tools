package cli_tools.task_manager.cli;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command.custom_command.repository.JsonCustomCommandRepository;
import cli_tools.common.cli.command_line.CommandLine;
import cli_tools.common.cli.command_line.Executor;
import cli_tools.common.cli.command_line.ExecutorImpl;
import cli_tools.common.cli.command_line.JlineCommandLine;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.command_parser.CommandParserFactoryImpl;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.configuration.ConfigurationRepositoryImpl;
import cli_tools.common.core.repository.*;
import cli_tools.common.label.repository.JsonLabelRepository;
import cli_tools.common.label.repository.JsonLabelRepositoryFactory;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.label.service.LabelServiceImpl;
import cli_tools.common.ordered_label.repository.JsonOrderedLabelRepositoryFactory;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.ordered_label.service.OrderedLabelServiceImpl;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_descriptor.repository.JsonPropertyDescriptorRepository;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorServiceImpl;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.repository.JsonStateRepository;
import cli_tools.common.temp_id_mapping.TempIDManager;
import cli_tools.common.util.RandomUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.view.repository.JsonViewInfoRepository;
import cli_tools.common.view.service.ViewInfoService;
import cli_tools.common.view.service.ViewInfoServiceImpl;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandDefinitionMixIn;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandParserFactoryImpl;
import cli_tools.task_manager.pseudo_property_provider.PseudoPropertyProviderMixIn;
import cli_tools.task_manager.task.repository.JsonTaskRepository;
import cli_tools.task_manager.task.repository.TaskRepository;
import cli_tools.task_manager.task.service.TaskService;
import cli_tools.task_manager.task.service.TaskServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaskManagerModule extends AbstractModule {
    Path basePath = Paths.get(System.getenv("TASK_MANAGER_BASE_PATH"));

    @Provides
    @Singleton
    CustomCommandRepository customCommandRepository() {
        JsonCustomCommandRepository jsonCustomCommandRepository =
                new JsonCustomCommandRepository(new File(basePath + "custom_command_definition.json"));
        jsonCustomCommandRepository.setMixIn(CustomCommandDefinitionMixIn.class);
        return jsonCustomCommandRepository;
    }

    @Provides
    @Singleton
    TaskService taskService(PropertyManager propertyManager,
                            UUIDGenerator uuidGenerator,
                            PropertyConverter propertyConverter,
                            TempIDManager tempIdManager) {
        return new TaskServiceImpl(
                new JsonTaskRepository(getJsonFile("task.json")),
                new JsonTaskRepository(getJsonFile("done_task.json")),
                propertyManager,
                uuidGenerator,
                propertyConverter,
                tempIdManager);
    }

    @Provides
    @Singleton
    PropertyDescriptorRepository propertyDescriptorRepository(TempIDManager tempIdManager) {
        return new JsonPropertyDescriptorRepository(
                getJsonFile("property_descriptor.json"),
                tempIdManager,
                PseudoPropertyProviderMixIn.class);
    }

    @Provides
    JlineCommandLine jlineCommandLine(Executor executor) {
        return new JlineCommandLine(executor, basePath);
    }

    @Override
    protected void configure() {
        if (basePath == null) {
            basePath = Paths.get(System.getProperty("user.home"), ".config/task-manager");
        }

        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class).asEagerSingleton();
        bind(CustomCommandParserFactory.class).to(CustomCommandParserFactoryImpl.class).asEagerSingleton();
        bind(TaskRepository.class).to(JsonTaskRepository.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(LabelRepository.class).to(JsonLabelRepository.class).asEagerSingleton();
        bind(OrderedLabelRepositoryFactory.class).to(JsonOrderedLabelRepositoryFactory.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(ConfigurationRepositoryImpl.class).asEagerSingleton();
        bind(StateRepository.class).to(JsonStateRepository.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class).asEagerSingleton();
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(OrderedLabelService.class).to(OrderedLabelServiceImpl.class).asEagerSingleton();
        bind(ViewInfoService.class).to(ViewInfoServiceImpl.class).asEagerSingleton();
        bind(PropertyDescriptorService.class).to(PropertyDescriptorServiceImpl.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(TempIDManager.class).asEagerSingleton();
        bind(Executor.class).to(ExecutorImpl.class);
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(Context.class).to(TaskManagerContext.class);
        bind(CommandLine.class).to(JlineCommandLine.class);

        bind(File.class).annotatedWith(Names.named("labelJsonFile")).toInstance(getJsonFile("label.json"));
        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(getJsonFile("task.json"));
        bind(File.class).annotatedWith(Names.named("tempIdMappingJsonFile")).toInstance(getJsonFile("temp_id_mapping.json"));
        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(getJsonFile("property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("viewJsonFile")).toInstance(getJsonFile("view.json"));
        bind(File.class).annotatedWith(Names.named("stateJsonFile")).toInstance(getJsonFile("state.json"));
        bind(File.class).annotatedWith(Names.named("propertyToStringConverterJsonFile")).toInstance(getJsonFile("property_to_string_converter.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(getJsonFile("config.yaml"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(basePath.toFile());
    }

    private File getJsonFile(String filename) {
        return basePath.resolve(filename).toFile();
    }

}
