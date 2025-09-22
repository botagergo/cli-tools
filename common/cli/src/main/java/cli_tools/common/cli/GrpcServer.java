package cli_tools.common.cli;

import cli_tools.common.cli.executor.Executor;
import cli_tools.common.core.util.Print;
import hu.botagergo.cli_tools.common.cli.protos.CliGrpc;
import hu.botagergo.cli_tools.common.cli.protos.CliOuterClass;
import io.grpc.Server;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Log4j2
public class GrpcServer {

    private final int port;
    private final Executor executor;
    private Server server;

    public GrpcServer(int port, Executor executor) {
        this.port = port;
        this.executor = executor;
    }

    public void start() throws IOException {
        HealthStatusManager healthStatusManager = new HealthStatusManager();
        server = io.grpc.ServerBuilder.forPort(port)
                .addService(healthStatusManager.getHealthService())
                .addService(new CliServiceImpl())
                .build();
        server.start();
        healthStatusManager.setStatus("", HealthCheckResponse.ServingStatus.SERVING);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    healthStatusManager.setStatus("", HealthCheckResponse.ServingStatus.NOT_SERVING);
                    GrpcServer.this.stop();
                } catch (InterruptedException e) {
                    Print.logException(e, log);
                }
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private class CliServiceImpl extends CliGrpc.CliImplBase {
        @Override
        public void executeCommand(CliOuterClass.Command request,
                                   StreamObserver<CliOuterClass.CommandOutput> responseObserver) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            executor.execute(request.getCommand());
            CliOuterClass.CommandOutput commandOutput = CliOuterClass.CommandOutput.newBuilder()
                    .setOutput(out.toString(StandardCharsets.UTF_8)).build();
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            responseObserver.onNext(commandOutput);
            responseObserver.onCompleted();
        }
    }
}
