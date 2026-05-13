package dst.ass2.service.auth.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.AuthenticationRequest;
import dst.ass2.service.api.auth.proto.AuthenticationResponse;
import dst.ass2.service.api.auth.proto.AuthServiceGrpc;
import dst.ass2.service.api.auth.proto.TokenValidationRequest;
import dst.ass2.service.api.auth.proto.TokenValidationResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {

    @Inject
    private IAuthenticationService authenticationService;

    @Override
    public void authenticate(AuthenticationRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            String token = authenticationService.authenticate(request.getEmail(), request.getPassword());
            AuthenticationResponse response = AuthenticationResponse.newBuilder()
                    .setToken(token)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchUserException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asRuntimeException());
        } catch (AuthenticationException e) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void validateToken(TokenValidationRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        TokenValidationResponse response = TokenValidationResponse.newBuilder()
                .setValid(authenticationService.isValid(request.getToken()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
