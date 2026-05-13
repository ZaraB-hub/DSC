package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.InvalidTripException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
@Named
public class InvalidTripExceptionMapper implements ExceptionMapper<InvalidTripException> {

    @Override
    public Response toResponse(InvalidTripException exception) {
        return Response.status(422).build();
    }
}
