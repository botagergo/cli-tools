package cli_tools.common.cli.executor;

import cli_tools.common.core.util.Print;
import hu.botagergo.cli_tools.common.cli.protos.CliGrpc;
import hu.botagergo.cli_tools.common.cli.protos.CliOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

@Log4j2
public class GrpcExecutor implements Executor {

    private final ManagedChannel channel;
    private final CliGrpc.CliBlockingStub cliServiceBlockingStub;
    private final HealthGrpc.HealthBlockingStub healthBlockingStub;
    private boolean shouldExit = false;

    private GrpcExecutor(ManagedChannel channel) {
        this.channel = channel;
        this.cliServiceBlockingStub = CliGrpc.newBlockingStub(channel);
        this.healthBlockingStub = HealthGrpc.newBlockingStub(channel);
    }

    public static GrpcExecutor create(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        return new GrpcExecutor(channel);
    }

    public void close() {
        this.channel.shutdown();
    }

    @Override
    public void execute(String commandStr) {
        if (commandStr.equals("exit")) {
            shouldExit = true;
            return;
        }
        CliOuterClass.Command command = CliOuterClass.Command.newBuilder().setCommand(commandStr).build();
        CliOuterClass.CommandOutput commandOutput;
        try {
            commandOutput = cliServiceBlockingStub.executeCommand(command);
        } catch (StatusRuntimeException e) {
            Print.printError("failed to connect to daemon: %s", e.getMessage());
            return;
        }
        Print.print(commandOutput.getOutput());
    }

    @Override
    public boolean shouldExit() {
        return shouldExit;
    }

    public boolean healthCheck() {
        try {
            HealthCheckResponse response =
                    healthBlockingStub.check(HealthCheckRequest.newBuilder().setService("").build());
            return response.getStatus() == HealthCheckResponse.ServingStatus.SERVING;
        } catch (StatusRuntimeException e) {
            log.debug("health check failed");
            Print.logException(e, log, Level.DEBUG);
            return false;
        }
    }

    public boolean waitUntilServerReady() throws InterruptedException {
        final int maxTries = 10;
        final int delay = 1000;
        for (int i = 0; i < maxTries; i++) {
            if (healthCheck()) {
                return true;
            }
            Thread.sleep(delay);
        }
        return false;
    }

}
