package cli_tools.task_manager.cli;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.cli.Context;
import cli_tools.common.cli.GrpcServer;
import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command_line.CommandLine;
import cli_tools.common.cli.command_line.JlineCommandLine;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.executor.Executor;
import cli_tools.common.cli.executor.GrpcExecutor;
import cli_tools.common.cli.executor.LocalExecutor;
import cli_tools.common.core.util.Print;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.task_manager.cli.command_parser.*;
import cli_tools.task_manager.cli.init.Initializer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class TaskManagerCLI {

    private static final String defaultDaemonHost = "localhost";
    private static final int defaultDaemonPort = 8224;

    public static void main(String @NonNull [] args) throws IOException, InterruptedException, URISyntaxException {
        Options options = new Options();
        options.addOption("h", "help", false, "Show help");
        options.addOption(null, "daemon", false, "Start daemon process");
        options.addOption(null, "connect-to-daemon", false, "Connect to an already running daemon");
        options.addOption(null, "standalone", false, "Run without daemon process");
        options.addOption(null, "daemon-host", true, "Hostname of daemon process");
        options.addOption(null, "daemon-port", true, "Port of daemon process");
        options.addOption(null, "database-mode", true, "The way data is stored - json or postgresql");
        options.addOption(null, "postgresql-url", true, "PostgreSQL database URL");
        options.addOption(null, "postgresql-username", true, "PostgreSQL database username");
        options.addOption(null, "postgresql-password", true, "PostgreSQL database password");

        DefaultParser parser = new DefaultParser();
        HelpFormatter formatter = HelpFormatter.builder()
                .setShowSince(false)
                .get();

        boolean isDaemon = false;
        boolean shouldConnectToDaemon = false;
        boolean isStandalone = false;
        String daemonHost;
        Integer daemonPort = null;

        org.apache.commons.cli.CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (AlreadySelectedException e) {
            Print.printError("Option not compatible with previous option(s): %s", e.getOption().getKey());
            return;
        } catch (AmbiguousOptionException e) {
            Print.printError("Unrecognized option: %s", e.getOption());
            return;
        } catch (ParseException e) {
            Print.printError(e.getMessage());
            return;
        }

        TaskManagerConfig taskManagerConfig = new TaskManagerConfig();

        for (Option opt : cmd.getOptions()) {
            if (!options.hasOption(opt.getLongOpt())) {
                Print.printError("Unrecognized option: %s", opt.getKey());
                return;
            }
        }

        if (cmd.hasOption("daemon") && cmd.hasOption("daemon-host")) {
            Print.printError("Option 'daemon-host' is not compatible with 'daemon'");
            return;
        }

        if (cmd.hasOption("standalone") &&
                (cmd.hasOption("daemon-host") || cmd.hasOption("daemon-port"))) {
            Print.printError("Options 'daemon-host' and 'daemon-port' are not compatible with 'daemon'");
            return;
        }

        if (cmd.hasOption("daemon")) {
            isDaemon = true;
        }
        if (cmd.hasOption("connect-to-daemon")) {
            shouldConnectToDaemon = true;
        }
        if (cmd.hasOption("standalone")) {
            isStandalone = true;
        }

        if ((isDaemon ? 1 : 0) + (shouldConnectToDaemon ? 1 : 0) + (isStandalone ? 1 : 0) > 1) {
            Print.printError("Options 'daemon', 'connect-to-daemon' and 'standalone' are mutually exclusive");
            return;
        }

        daemonHost = cmd.getOptionValue("daemon-host", () -> null);
        if (cmd.hasOption("daemon-port")) {
            String portStr = cmd.getOptionValue("daemon-port");
            try {
                daemonPort = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                Print.printError("Not a valid port: %", portStr);
                return;
            }
            if (!(0 <= daemonPort && daemonPort <= 65535)) {
                Print.printError("Not a valid port: %", portStr);
                return;
            }
        }

        if (cmd.hasOption("database-mode")) {
            String databaseMode = cmd.getOptionValue("database-mode");
            switch (databaseMode) {
                case "json" -> taskManagerConfig.setDatabaseMode(TaskManagerConfig.DatabaseMode.JSON);
                case "postgresql" -> taskManagerConfig.setDatabaseMode(TaskManagerConfig.DatabaseMode.POSTGRESQL);
                default -> {
                    Print.printError("Not a valid database mode: %", databaseMode);
                    return;
                }
            }
        } else {
            taskManagerConfig.setDatabaseMode(TaskManagerConfig.DatabaseMode.JSON);
        }

        if (cmd.hasOption("postgresql-url")) {
            taskManagerConfig.setPostgresqlUrl(cmd.getOptionValue("postgresql-url"));
        }
        if (cmd.hasOption("postgresql-username")) {
            taskManagerConfig.setPostgresqlUsername(cmd.getOptionValue("postgresql-username"));
        }
        if (cmd.hasOption("postgresql-password")) {
            taskManagerConfig.setPostgresqlPassword(cmd.getOptionValue("postgresql-password"));
        }

        if (cmd.getArgs().length > 0) {
            Print.printError("Positional arguments are not expected");
            return;
        }

        if (cmd.hasOption("h")) {
            formatter.printHelp("task_manager [--daemon|--connect-to-daemon|--standalone] [options]", null, options, null, false);
            return;
        }

        if (taskManagerConfig.getProfile() == null) {
            taskManagerConfig.setProfile("default");
        }
        if (taskManagerConfig.getPostgresqlUrl() == null) {
            taskManagerConfig.setPostgresqlUrl("jdbc:postgresql://postgres:5432/task_manager_db");
        }
        if (taskManagerConfig.getPostgresqlUsername() == null) {
            taskManagerConfig.setPostgresqlUsername("postgres");
        }
        if (taskManagerConfig.getPostgresqlPassword() == null) {
            taskManagerConfig.setPostgresqlPassword("12345");
        }

        Injector injector = Guice.createInjector(new TaskManagerModule(taskManagerConfig));

        if (taskManagerConfig.getDatabaseMode() == TaskManagerConfig.DatabaseMode.POSTGRESQL) {
            Flyway flyway = Flyway.configure()
                    .dataSource(injector.getInstance(DataSource.class))
                    .load();
            flyway.migrate();
        }

        if (isDaemon || isStandalone) {
            if (!init(injector)) {
                return;
            }
            try {
                initDefaultDataIfNeeded(injector);
            } catch (ServiceException e) {
                Print.printError("Failed to initialize default data: %s".formatted(e.getMessage()), e);
                Print.logException(e, log);
                return;
            }
        }

        if (isDaemon) {
            runDaemon(injector, daemonPort);
        } else if (shouldConnectToDaemon) {
            connectToDaemon(daemonHost, daemonPort);
        } else if (isStandalone) {
            runStandalone(injector);
        } else {
            runWithDaemon(daemonPort, injector);
        }
    }

    private static void runWithDaemon(Integer daemonPort, Injector injector) throws IOException, URISyntaxException, InterruptedException {
        if (daemonPort == null) {
            daemonPort = defaultDaemonPort;
        }

        GrpcExecutor executor = GrpcExecutor.create(defaultDaemonHost, daemonPort);
        if (!executor.healthCheck()) {
            startDaemon(daemonPort);
            if (!executor.waitUntilServerReady()) {
                Print.printWarning("Failed to start daemon process, running standalone");

                if (!init(injector)) {
                    return;
                }

                runStandalone(injector);
                return;
            }
            Print.printInfo("Started daemon on %s:%d", defaultDaemonHost, daemonPort);
        }

        JlineCommandLine commandLine = new JlineCommandLine(executor, null, null);
        commandLine.run();

        executor.close();
    }

    private static void connectToDaemon(String daemonHost, Integer daemonPort) throws IOException {
        if (daemonHost == null) {
            daemonHost = defaultDaemonHost;
        }
        if (daemonPort == null) {
            daemonPort = defaultDaemonPort;
        }

        GrpcExecutor executor = GrpcExecutor.create(daemonHost, daemonPort);

        if (!executor.healthCheck()) {
            Print.printError("Daemon is not running on %s:%d", daemonHost, daemonPort);
            return;
        }

        Print.printInfo("Connected to daemon running on %s:%d", daemonHost, daemonPort);

        JlineCommandLine commandLine = new JlineCommandLine(executor, null, null);
        commandLine.run();
    }

    private static void runDaemon(Injector injector, Integer daemonPort) {
        if (daemonPort == null) {
            daemonPort = defaultDaemonPort;
        }

        try {
            Executor executor = injector.getInstance(LocalExecutor.class);
            GrpcServer grpcServer = new GrpcServer(daemonPort, executor);

            grpcServer.start();

            Print.printInfo("Daemon listening on %s:%d", defaultDaemonHost, daemonPort);

            grpcServer.blockUntilShutdown();
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runStandalone(Injector injector) {
        CommandLine commandLine = injector.getInstance(CommandLine.class);
        try {
            commandLine.run();
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    //noinspection BooleanMethodIsAlwaysInverted
    private static boolean init(Injector injector) throws IOException {
        CommandParserFactory commandParserFactory = injector.getInstance(CommandParserFactory.class);
        CustomCommandRepository customCommandRepository = injector.getInstance(CustomCommandRepository.class);
        CustomCommandParserFactory customCommandParserFactory = injector.getInstance(CustomCommandParserFactory.class);
        Context context = injector.getInstance(Context.class);

        try {
            initDefaultDataIfNeeded(injector);
        } catch (ServiceException e) {
            Print.printError("Failed to initialize default data: %s".formatted(e.getMessage()), e);
            Print.logException(e, log);
            return false;
        }

        commandParserFactory.registerParser("add", AddTaskCommandParser::new);
        commandParserFactory.registerParser("list", ListTasksCommandParser::new);
        commandParserFactory.registerParser("done", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("undone", UndoneTaskCommandParser::new);
        commandParserFactory.registerParser("clear", ClearCommandParser::new);
        commandParserFactory.registerParser("delete", DeleteTaskCommandParser::new);
        commandParserFactory.registerParser("modify", ModifyTaskCommandParser::new);
        commandParserFactory.registerParser("ai", AICommandParser::new);
        commandParserFactory.registerParser("addLabel", AddLabelCommandParser::new);
        commandParserFactory.registerParser("listLabel", ListLabelCommandParser::new);
        commandParserFactory.registerParser("deleteLabel", DeleteLabelCommandParser::new);

        List<PropertyDescriptor> propertyDescriptors;
        try {
            propertyDescriptors = context.getPropertyDescriptorService().getPropertyDescriptors();
        } catch (ServiceException e) {
            Print.printError("Failed to load property descriptors: %s".formatted(e.getMessage()), e);
            Print.logException(e, log);
            return false;
        }

        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));

        for (CustomCommandDefinition customCommandDefinition : customCommandRepository.getAll()) {
            commandParserFactory.registerParser(customCommandDefinition.getCommandName(),
                    () -> customCommandParserFactory.createParser(customCommandDefinition));
        }

        return true;
    }

    private static void initDefaultDataIfNeeded(Injector injector) throws IOException, ServiceException {
        Initializer initializer = injector.getInstance(Initializer.class);
        String profile = Objects.requireNonNullElse(System.getenv("TASK_MANAGER_PROFILE"), "default");
        File initializedFile = Paths.get(OsDirs.getDataDir(profile).toString(), ".initialized").toFile();

        if (!initializedFile.isFile()) {
            log.info("Initializing database with default data");

            if (!OsDirs.getDataDir(profile).mkdirs()) {
                throw new IOException("Failed to create data directory: %s".formatted(OsDirs.getDataDir(profile).getAbsolutePath()));
            }

            initializer.initialize();
            log.debug("Creating file {}}", initializedFile.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            initializedFile.createNewFile();
        } else {
            log.info("Database already initialized");
        }
    }

    public static void startDaemon(int daemonPort) throws IOException, URISyntaxException {
        String jarPath = getRunningJar();
        List<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + "/bin/java");
        command.add("-jar");
        command.add(jarPath);
        command.add("--daemon");
        command.add("--daemon-port");
        command.add(Integer.toString(daemonPort));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.start();
    }

    public static String getRunningJar() throws URISyntaxException {
        return new File(TaskManagerCLI.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getAbsolutePath();
    }

}
