package dst.ass2.service.facade.impl;

import dst.ass2.service.auth.client.IAuthenticationClient;
import dst.ass2.service.facade.filter.IAuthenticationFilter;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@ManagedBean
@Provider
public class AuthenticationFilter implements IAuthenticationFilter, ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private IAuthenticationClient authClient;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isAuthenticationRequest(requestContext)) {
            return;
        }

        String token = extractToken(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
        if (token == null || !authClient.isTokenValid(token)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    @Override
    public void setAuthClient(IAuthenticationClient client) {
        this.authClient = client;
    }

    private boolean isAuthenticationRequest(ContainerRequestContext requestContext) {
        UriInfo uriInfo = requestContext.getUriInfo();
        if (uriInfo == null || uriInfo.getPath() == null) {
            return false;
        }
        return uriInfo.getPath().startsWith("auth");
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
