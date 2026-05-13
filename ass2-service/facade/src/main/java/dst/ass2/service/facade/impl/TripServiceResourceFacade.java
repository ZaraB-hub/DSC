package dst.ass2.service.facade.impl;

import dst.ass2.service.api.trip.DriverNotAvailableException;
import dst.ass2.service.api.trip.EntityNotFoundException;
import dst.ass2.service.api.trip.InvalidTripException;
import dst.ass2.service.api.trip.MatchDTO;
import dst.ass2.service.api.trip.TripInfoDTO;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import dst.ass2.service.facade.trip.ITripServiceResourceFacade;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.net.URL;

@ManagedBean
@Provider
@Path("/trips")
public class TripServiceResourceFacade implements ITripServiceResourceFacade {

    @Inject
    private URI tripServiceURI;

    private ITripServiceResource delegate;

    @PostConstruct
    public void initDelegate() {
        ClientConfig config = new ClientConfig()
                .connectorProvider(new HttpUrlConnectorProvider())
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        delegate = WebResourceFactory.newResource(
                ITripServiceResource.class,
                ClientBuilder.newClient(config).target(tripServiceURI.toString()));
//        delegate = WebResourceFactory.newResource(ITripServiceResource.class, ClientBuilder.newClient().target(tripServiceURI));
    }

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        return delegate.createTrip(riderId, pickupId, destinationId);
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        return delegate.confirm(tripId);
    }

    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        return delegate.getTrip(tripId);
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        return delegate.deleteTrip(tripId);
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return delegate.addStop(tripId, locationId);
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException {
        return delegate.removeStop(tripId, locationId);
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        return delegate.match(tripId, matchDTO);
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        return delegate.complete(tripId, tripInfoDTO);
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        return delegate.cancel(tripId);
    }

    @Override
    public ITripServiceResource getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(ITripServiceResource delegate) {
        this.delegate = delegate;
    }
}
