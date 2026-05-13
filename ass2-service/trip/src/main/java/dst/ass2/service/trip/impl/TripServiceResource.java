package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.DriverNotAvailableException;
import dst.ass2.service.api.trip.EntityNotFoundException;
import dst.ass2.service.api.trip.ITripService;
import dst.ass2.service.api.trip.InvalidTripException;
import dst.ass2.service.api.trip.MatchDTO;
import dst.ass2.service.api.trip.TripDTO;
import dst.ass2.service.api.trip.TripInfoDTO;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Singleton
@Named
@Path("/trips")
public class TripServiceResource implements ITripServiceResource {

    @Inject
    private ITripService tripService;

    @Override
    public Response createTrip(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        TripDTO trip = tripService.create(riderId, pickupId, destinationId);
        return Response.status(201).entity(trip.getId()).build();
    }

    @Override
    public Response confirm(Long tripId) throws EntityNotFoundException, InvalidTripException {
        tripService.confirm(tripId);
        return Response.noContent().build();
    }

    @Override
    public Response getTrip(Long tripId) throws EntityNotFoundException {
        TripDTO trip = tripService.find(tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found: " + tripId);
        }
        return Response.ok(trip).build();
    }

    @Override
    public Response deleteTrip(Long tripId) throws EntityNotFoundException {
        tripService.delete(tripId);
        return Response.noContent().build();
    }

    @Override
    public Response addStop(Long tripId, Long locationId) throws EntityNotFoundException {
        TripDTO trip = findRequiredTrip(tripId);
        if (!tripService.addStop(trip, locationId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(trip.getFare()).build();
    }

    @Override
    public Response removeStop(Long tripId, Long locationId) throws EntityNotFoundException {
        TripDTO trip = findRequiredTrip(tripId);
        if (!tripService.removeStop(trip, locationId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.noContent().build();
    }

    @Override
    public Response match(Long tripId, MatchDTO matchDTO) throws EntityNotFoundException, DriverNotAvailableException {
        tripService.match(tripId, matchDTO);
        return Response.noContent().build();
    }

    @Override
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        tripService.complete(tripId, tripInfoDTO);
        return Response.noContent().build();
    }

    @Override
    public Response cancel(Long tripId) throws EntityNotFoundException {
        tripService.cancel(tripId);
        return Response.noContent().build();
    }

    private TripDTO findRequiredTrip(Long tripId) throws EntityNotFoundException {
        TripDTO trip = tripService.find(tripId);
        if (trip == null) {
            throw new EntityNotFoundException("Trip not found: " + tripId);
        }
        return trip;
    }
}
