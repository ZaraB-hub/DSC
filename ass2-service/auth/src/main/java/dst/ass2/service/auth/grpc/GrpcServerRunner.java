package dst.ass2.service.auth.grpc;

import dst.ass2.service.auth.impl.AuthService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
@Named
public class GrpcServerRunner implements IGrpcServerRunner {

    @Inject
    private GrpcServerProperties properties;

    @Inject
    private AuthService authService;

    private Server server;

    @Override
    public void run() throws IOException {
        if (server != null && !server.isShutdown()) {
            return;
        }

        server = ServerBuilder.forPort(properties.getPort())
                .directExecutor()
                .addService(authService)
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.shutdown();
            }
        }));
    }
}
