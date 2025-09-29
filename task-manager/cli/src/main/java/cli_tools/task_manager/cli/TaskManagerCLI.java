package cli_tools.task_manager.cli;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.GrpcServer;
import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command_line.*;
import cli_tools.common.cli.command_line.CommandLine;
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

        if (cmd.getArgs().length > 0) {
            Print.printError("Positional arguments are not expected");
            return;
        }

        if (cmd.hasOption("h")) {
            formatter.printHelp("task_manager [--daemon|--connect-to-daemon|--standalone] [options]", null, options, null, false);
            return;
        }

        Injector injector = null;

        if (isDaemon || isStandalone) {
            injector = init();
            if (injector == null) {
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
            runWithDaemon(daemonPort);
        }
    }

    private static void runWithDaemon(Integer daemonPort) throws IOException, URISyntaxException, InterruptedException {
        if (daemonPort == null) {
            daemonPort = defaultDaemonPort;
        }

        GrpcExecutor executor = GrpcExecutor.create(defaultDaemonHost, daemonPort);
        if (!executor.healthCheck()) {
            startDaemon(daemonPort);
            if (!executor.waitUntilServerReady()) {
                Print.printWarning("Failed to start daemon process, running standalone");
                Injector injector = init();
                if (injector != null) {
                    runStandalone(injector);
                }
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

            Print.printInfo("Daemon listening on %s:%d", defaultDaemonHost, daemonPort);
            grpcServer.start();

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

    private static Injector init() throws IOException {
        String profile = Objects.requireNonNullElse(System.getenv("TASK_MANAGER_PROFILE"), "default");
        Injector injector = Guice.createInjector(new TaskManagerModule(profile));
        Initializer initializer = injector.getInstance(Initializer.class);
        CommandParserFactory commandParserFactory = injector.getInstance(CommandParserFactory.class);
        CustomCommandRepository customCommandRepository = injector.getInstance(CustomCommandRepository.class);
        CustomCommandParserFactory customCommandParserFactory = injector.getInstance(CustomCommandParserFactory.class);
        Context context = injector.getInstance(Context.class);

        File initializedFile = Paths.get(OsDirs.getDataDir(profile).toString(), ".initialized").toFile();
        if (!initializedFile.isFile()) {
            try {
                initializer.initialize();
                //noinspection ResultOfMethodCallIgnored
                initializedFile.createNewFile();
            } catch (IOException e) {
                Print.printError(e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
                return null;
            }
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

        List<PropertyDescriptor> propertyDescriptors = context.getPropertyDescriptorService().getPropertyDescriptors();
        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));

        try {
            for (CustomCommandDefinition customCommandDefinition : customCommandRepository.getAll()) {
                commandParserFactory.registerParser(customCommandDefinition.getCommandName(),
                        () -> customCommandParserFactory.createParser(customCommandDefinition));
            }
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return injector;
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
