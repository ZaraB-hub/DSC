package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.AuthServiceGrpc;
import dst.ass2.service.api.auth.proto.AuthenticationRequest;
import dst.ass2.service.api.auth.proto.TokenValidationRequest;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import javax.inject.Inject;
import javax.inject.Named;


public class GrpcAuthenticationClient implements IAuthenticationClient {

    private final ManagedChannel channel;
    private final AuthServiceGrpc.AuthServiceBlockingStub stub;


    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        this.channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .directExecutor()
                .build();
        this.stub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        AuthenticationRequest request = AuthenticationRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();

        try {
            return stub.authenticate(request).getToken();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NoSuchUserException(e);
            }
            if (e.getStatus().getCode() == Status.Code.UNAUTHENTICATED) {
                throw new AuthenticationException(e);
            }
            throw e;
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        TokenValidationRequest request = TokenValidationRequest.newBuilder()
                .setToken(token)
                .build();
        return stub.validateToken(request).getValid();
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
