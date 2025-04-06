package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.cli.Context;
import cli_tools.common.core.repository.*;
import cli_tools.common.repository.JsonStateRepository;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command_parser.*;
import cli_tools.task_manager.task.repository.TaskRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.label.service.LabelServiceImpl;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.ordered_label.service.OrderedLabelServiceImpl;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorServiceImpl;
import cli_tools.task_manager.task.service.TaskService;
import cli_tools.task_manager.task.service.TaskServiceImpl;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.temp_id_mapping.service.TempIDMappingServiceImpl;
import cli_tools.common.view.service.ViewInfoService;
import cli_tools.common.view.service.ViewInfoServiceImpl;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.label.repository.JsonLabelRepositoryFactory;
import cli_tools.common.ordered_label.repository.JsonOrderedLabelRepositoryFactory;
import cli_tools.common.property_descriptor.repository.JsonPropertyDescriptorRepository;
import cli_tools.task_manager.task.repository.JsonTaskRepository;
import cli_tools.common.temp_id_mapping.repository.JsonTempIDMappingRepository;
import cli_tools.common.view.repository.JsonViewInfoRepository;
import cli_tools.common.cli.command_line.Executor;
import cli_tools.common.cli.command_line.ExecutorImpl;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.command_parser.CommandParserFactoryImpl;
import cli_tools.common.util.RandomUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestModule extends AbstractModule {
    @Provides
    protected CommandParserFactory getCommandParserFactory(ConfigurationRepository configurationRepository) {
        CommandParserFactory commandParserFactory = new CommandParserFactoryImpl(configurationRepository);

        commandParserFactory.registerParser("add", AddTaskCommandParser::new);
        commandParserFactory.registerParser("list", ListTasksCommandParser::new);
        commandParserFactory.registerParser("done", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("clear", ClearCommandParser::new);
        commandParserFactory.registerParser("delete", DeleteTaskCommandParser::new);
        commandParserFactory.registerParser("modify", ModifyTaskCommandParser::new);
        commandParserFactory.registerParser("ai", AICommandParser::new);

        return commandParserFactory;
    }

    @Override
    protected void configure() {
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("task-manager-ft-basic");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String basePath = tempDir.toString();

        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(TaskRepository.class).to(JsonTaskRepository.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(LabelRepositoryFactory.class).to(JsonLabelRepositoryFactory.class).asEagerSingleton();
        bind(OrderedLabelRepositoryFactory.class).to(JsonOrderedLabelRepositoryFactory.class).asEagerSingleton();
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(MockConfigurationRepository.class).asEagerSingleton();
        bind(StateRepository.class).to(JsonStateRepository.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class).asEagerSingleton();
        bind(TaskService.class).to(TaskServiceImpl.class).asEagerSingleton();
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(OrderedLabelService.class).to(OrderedLabelServiceImpl.class).asEagerSingleton();
        bind(ViewInfoService.class).to(ViewInfoServiceImpl.class).asEagerSingleton();
        bind(PropertyDescriptorService.class).to(PropertyDescriptorServiceImpl.class).asEagerSingleton();
        bind(TempIDMappingService.class).to(TempIDMappingServiceImpl.class).asEagerSingleton();
        bind(TempIDMappingRepository.class).to(JsonTempIDMappingRepository.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(Executor.class).to(ExecutorImpl.class);
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(Context.class).to(TaskManagerContext.class);

        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(new File(basePath + "task.json"));
        bind(File.class).annotatedWith(Names.named("tempIdMappingJsonFile")).toInstance(new File(basePath + "temp_id_mapping.json"));
        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(new File(basePath + "property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("viewInfoJsonFile")).toInstance(new File(basePath + "view_info.json"));
        bind(File.class).annotatedWith(Names.named("stateJsonFile")).toInstance(new File(basePath + "state.json"));
        bind(File.class).annotatedWith(Names.named("propertyToStringConverterJsonFile")).toInstance(new File(basePath + "property_to_string_converter.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(new File(basePath + "config.yaml"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(basePath));
    }

}
