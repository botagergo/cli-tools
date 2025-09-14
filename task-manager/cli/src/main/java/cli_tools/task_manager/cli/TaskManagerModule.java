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
import cli_tools.common.label.service.LabelService;
import cli_tools.common.label.service.LabelServiceImpl;
import cli_tools.common.ordered_label.repository.JsonOrderedLabelRepository;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.ordered_label.service.OrderedLabelServiceImpl;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_descriptor.repository.JsonPropertyDescriptorRepository;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorServiceImpl;
import cli_tools.common.property_lib.PropertyManager;
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
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TaskManagerModule extends AbstractModule {
    private final String profileName;

    public TaskManagerModule(String profileName) {
        this.profileName = profileName;
    }

    @Provides
    @Singleton
    CustomCommandRepository customCommandRepository() throws IOException {
        JsonCustomCommandRepository jsonCustomCommandRepository =
                new JsonCustomCommandRepository(OsDirs.getFile(OsDirs.DirType.DATA, profileName, "custom_command_definition.json"));
        jsonCustomCommandRepository.setMixIn(CustomCommandDefinitionMixIn.class);
        return jsonCustomCommandRepository;
    }

    @Provides
    @Singleton
    TaskService taskService(
            PropertyManager propertyManager,
            UUIDGenerator uuidGenerator,
            PropertyConverter propertyConverter,
            TempIDManager tempIdManager) throws IOException {
        return new TaskServiceImpl(
                new JsonTaskRepository(OsDirs.getFile(OsDirs.DirType.DATA, profileName, "task.json")),
                new JsonTaskRepository(OsDirs.getFile(OsDirs.DirType.DATA, profileName, "done_task.json")),
                propertyManager,
                uuidGenerator,
                propertyConverter,
                tempIdManager);
    }

    @Provides
    @Singleton
    PropertyDescriptorRepository propertyDescriptorRepository(
            TempIDManager tempIdManager,
            @Named("propertyDescriptorJsonFile") File propertyDescriptorFile
    ) {
        return new JsonPropertyDescriptorRepository(
                propertyDescriptorFile,
                tempIdManager,
                PseudoPropertyProviderMixIn.class);
    }

    @Provides
    JlineCommandLine jlineCommandLine(Executor executor) throws IOException {
        return new JlineCommandLine(executor, OsDirs.getFile(OsDirs.DirType.CACHE, profileName, "history"));
    }

    @SneakyThrows
    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class).asEagerSingleton();
        bind(CustomCommandParserFactory.class).to(CustomCommandParserFactoryImpl.class).asEagerSingleton();
        bind(TaskRepository.class).to(JsonTaskRepository.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(LabelRepository.class).to(JsonLabelRepository.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(ConfigurationRepositoryImpl.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class).asEagerSingleton();
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(OrderedLabelRepository.class).to(JsonOrderedLabelRepository.class).asEagerSingleton();
        bind(OrderedLabelService.class).to(OrderedLabelServiceImpl.class).asEagerSingleton();
        bind(ViewInfoService.class).to(ViewInfoServiceImpl.class).asEagerSingleton();
        bind(PropertyDescriptorService.class).to(PropertyDescriptorServiceImpl.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(TempIDManager.class).asEagerSingleton();
        bind(Executor.class).to(ExecutorImpl.class);
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(Context.class).to(TaskManagerContext.class);
        bind(CommandLine.class).to(JlineCommandLine.class);

        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName,"property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("labelJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName,"label.json"));
        bind(File.class).annotatedWith(Names.named("orderedLabelJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName,"ordered_label.json"));
        bind(File.class).annotatedWith(Names.named("viewJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName,"view.json"));
        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName,"task.json"));
        bind(File.class).annotatedWith(Names.named("propertyToStringConverterJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, profileName, "property_to_string_converter.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(OsDirs.getFile(OsDirs.DirType.CONFIG, profileName,"config.yaml"));
    }

}
