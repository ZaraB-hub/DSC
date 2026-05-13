package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.model.impl.Driver;
import dst.ass1.jpa.model.impl.Location;
import dst.ass1.jpa.model.impl.Match;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass1.jpa.model.impl.TripInfo;
import dst.ass1.jpa.model.impl.TripReceipt;
import dst.ass1.jpa.model.impl.Vehicle;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Transactional
@Named
public class TripService implements ITripService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private IMatchingService matchingService;

    @Inject
    private IDAOFactory daoFactory;

    @Inject
    private IModelFactory modelFactory;


    private TripDTO toDTO(ITrip trip) {
        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        dto.setRiderId(trip.getRider() == null ? null : trip.getRider().getId());
        dto.setPickupId(trip.getPickup() == null ? null : trip.getPickup().getId());
        dto.setDestinationId(trip.getDestination() == null ? null : trip.getDestination().getId());
        dto.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        IRider rider = findRequired(Rider.class, riderId);
        ILocation pickup = findRequired(Location.class, pickupId);
        ILocation destination = findRequired(Location.class, destinationId);

        ITrip trip = modelFactory.createTrip();
        trip.setState(TripState.CREATED);
        trip.setRider(rider);
        trip.setPickup(pickup);
        trip.setDestination(destination);
        trip.setCreated(new Date());
        trip.setUpdated(new Date());

        entityManager.persist(trip);
        entityManager.flush();

        TripDTO dto = toDTO(trip);
        refreshFare(dto);
        return dto;
    }

    private void refreshFare(TripDTO trip) {
        try {
            trip.setFare(matchingService.calculateFare(trip));
        } catch (Exception e) {
            trip.setFare(null);
        }
    }

    @Override
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        ITrip trip = findRequired(Trip.class, tripId);
        assertHasRider(trip);
        assertState(trip, TripState.CREATED);

        TripDTO dto = toDTO(trip);
        MoneyDTO fare = matchingService.calculateFare(dto);
        if (fare == null) {
            throw new InvalidTripException();
        }
        dto.setFare(fare);

        trip.setState(TripState.QUEUED);
        trip.setUpdated(new Date());
        matchingService.queueTripForMatching(tripId);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        try {
            ITrip trip = entityManager.find(Trip.class, tripId, LockModeType.PESSIMISTIC_WRITE);
            if (trip == null) {
                throw new EntityNotFoundException("Trip not found: " + tripId);
            }
            assertHasRider(trip);
            assertState(trip, TripState.QUEUED);

            IDriver driver = findRequiredLocked(Driver.class, match.getDriverId());
            IVehicle vehicle = findRequiredLocked(Vehicle.class, match.getVehicleId());
            if (isDriverAssigned(driver.getId())) {
                throw new DriverNotAvailableException("Driver not available: " + driver.getId());
            }

            IMatch entity = modelFactory.createMatch();
            entity.setDate(new Date());
            entity.setTrip(trip);
            entity.setDriver(driver);
            entity.setVehicle(vehicle);
            entity.setFare(toMoney(match.getFare()));

            trip.setMatch(entity);
            trip.setState(TripState.MATCHED);
            trip.setUpdated(new Date());
            entityManager.persist(entity);
        } catch (EntityNotFoundException | DriverNotAvailableException | IllegalStateException ex) {
            matchingService.queueTripForMatching(tripId);
            throw ex;
        }
    }


    @Override
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        ITrip trip = findRequired(Trip.class, tripId);

        ITripInfo tripInfo = modelFactory.createTripInfo();
        tripInfo.setDistance(tripInfoDTO.getDistance());
        tripInfo.setCompleted(tripInfoDTO.getCompleted());
        tripInfo.setTrip(trip);

        if (tripInfoDTO.getFare() != null) {
            ITripReceipt receipt = modelFactory.createTripReceipt();
            IMoney money = modelFactory.createMoney();
            money.setCurrency(tripInfoDTO.getFare().getCurrency());
            money.setCurrencyValue(tripInfoDTO.getFare().getValue());
            receipt.setTotal(money);
            entityManager.persist(receipt);
            tripInfo.setReceipt(receipt);
        }

        entityManager.persist(tripInfo);

        trip.setTripInfo(tripInfo);
        trip.setState(TripState.COMPLETED);
        trip.setUpdated(new Date());
    }
    @Override
    public void cancel(Long tripId) throws EntityNotFoundException {
        ITrip trip = findRequired(Trip.class, tripId);
        trip.setState(TripState.CANCELLED);
        trip.setUpdated(new Date());
    }

    @Override
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        ITrip entity = findRequired(Trip.class, trip.getId());
        ILocation location = findRequired(Location.class, locationId);
        assertState(entity, TripState.CREATED);

        if (isPickupOrDestination(trip, locationId) || trip.getStops().contains(locationId)) {
            return false;
        }

        entity.addStop(location);
        entity.setUpdated(new Date());
        trip.getStops().add(locationId);
        refreshFare(trip);
        return true;
    }

    @Override
    @Transactional
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        ITrip entity = findRequired(Trip.class, trip.getId());
        findRequired(Location.class, locationId);
        assertState(entity, TripState.CREATED);

        if (isPickupOrDestination(trip, locationId) || !trip.getStops().contains(locationId)) {
            return false;
        }

        entity.setStops(entity.getStops().stream()
                .filter(stop -> !locationId.equals(stop.getId()))
                .collect(Collectors.toList()));
        entity.setUpdated(new Date());
        trip.getStops().remove(locationId);
        refreshFare(trip);
        return true;
    }

    @Override
    @Transactional
    public void delete(Long tripId) throws EntityNotFoundException {
        ITrip trip = findRequired(Trip.class, tripId);
        entityManager.remove(trip);
    }

    @Override
    @Transactional
    public TripDTO find(Long tripId) {
        ITrip trip = entityManager.find(Trip.class, tripId);
        if (trip == null) {
            return null;
        }

        TripDTO dto = toDTO(trip);
        refreshFare(dto);
        return dto;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public IMatchingService getMatchingService() {
        return matchingService;
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private <T> T findRequired(Class<T> type, Long id) throws EntityNotFoundException {
        T entity = entityManager.find(type, id);
        if (entity == null) {
            throw new EntityNotFoundException(type.getSimpleName() + " not found: " + id);
        }
        return entity;
    }

    private <T> T findRequiredLocked(Class<T> type, Long id) throws EntityNotFoundException {
        T entity = entityManager.find(type, id, LockModeType.PESSIMISTIC_WRITE);
        if (entity == null) {
            throw new EntityNotFoundException(type.getSimpleName() + " not found: " + id);
        }
        return entity;
    }

    private void assertHasRider(ITrip trip) {
        if (trip.getRider() == null) {
            throw new IllegalStateException();
        }
    }

    private void assertState(ITrip trip, TripState expected) {
        if (trip.getState() != expected) {
            throw new IllegalStateException();
        }
    }

    private boolean isPickupOrDestination(TripDTO trip, Long locationId) {
        return locationId.equals(trip.getPickupId()) || locationId.equals(trip.getDestinationId());
    }

    private boolean isDriverAssigned(Long driverId) {
        List<TripState> activeStates = List.of(TripState.MATCHED, TripState.APPROACHING, TripState.IN_PROGRESS);
        Long matches = entityManager.createQuery(
                        "select count(m) from Match m where m.driver.id = :driverId and m.trip.state in :states",
                        Long.class)
                .setParameter("driverId", driverId)
                .setParameter("states", activeStates)
                .getSingleResult();
        return matches > 0;
    }


    private IMoney toMoney(MoneyDTO dto) {
        if (dto == null) {
            return null;
        }
        IMoney money = modelFactory.createMoney();
        money.setCurrency(dto.getCurrency());
        money.setCurrencyValue(dto.getValue());
        return money;
    }
}
