package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.DriverNotAvailableException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
@Named
public class DriverNotAvailableExceptionMapper implements ExceptionMapper<DriverNotAvailableException> {

    @Override
    public Response toResponse(DriverNotAvailableException exception) {
        return Response.status(Response.Status.CONFLICT).build();
    }
}
