package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.client.IAuthenticationClient;
import dst.ass2.service.facade.auth.IAuthenticationResourceFacade;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@ManagedBean
@Provider
@Path("/auth")
public class AuthenticationResourceFacade implements IAuthenticationResourceFacade {

    @Inject
    private IAuthenticationClient delegate;

    @Override
    public Response authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        try {
            return Response.ok(delegate.authenticate(email, password)).build();
        } catch (NoSuchUserException | AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Override
    public IAuthenticationClient getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(IAuthenticationClient delegate) {
        this.delegate = delegate;
    }
}
