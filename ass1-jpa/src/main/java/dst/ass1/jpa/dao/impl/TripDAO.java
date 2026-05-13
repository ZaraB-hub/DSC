package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.*;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TripDAO implements ITripDAO {
    private final EntityManager em;

    public TripDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<ITrip> findCompletedTripsWithMinStops(int minStops) {
        if (minStops < 0) {
            throw new IllegalArgumentException();
        }
        return (List<ITrip>) (List<?>) em.createNamedQuery(Constants.Q_TRIP_COMPLETED_MIN_STOPS, Trip.class)
                .setParameter("state", TripState.COMPLETED)
                .setParameter("minStops", minStops)
                .getResultList();
    }

    @Override
    public List<ITrip> findTripsWithCriteria(BigDecimal minFare, BigDecimal maxFare, Double minDriverRating, Long minStops) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> query = cb.createQuery(Trip.class);
        Root<Trip> trip = query.from(Trip.class);

        List<Predicate> predicates = new ArrayList<>();

        if (minFare != null || maxFare != null) {
            Join<Trip, TripInfo> tripInfo = trip.join("tripInfo", JoinType.LEFT);
            Join<TripInfo, TripReceipt> receipt = tripInfo.join("receipt", JoinType.LEFT);

            if (minFare != null) {
                predicates.add(cb.greaterThanOrEqualTo(receipt.get("total").get("currencyValue"), minFare));
            }
            if (maxFare != null) {
                predicates.add(cb.lessThanOrEqualTo(receipt.get("total").get("currencyValue"), maxFare));
            }
        }

        if (minDriverRating != null) {
            Join<Trip, Match> match = trip.join("match", JoinType.LEFT);
            Join<Match, Driver> driver = match.join("driver", JoinType.LEFT);
            predicates.add(cb.greaterThanOrEqualTo(driver.get("avgRating"), minDriverRating));
        }

        if (minStops != null) {
            predicates.add(cb.ge(cb.size(trip.get("stops")), minStops.intValue()));
        }

        query.select(trip)
                .distinct(true)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(trip.get("created")));

        return (List<ITrip>) (List<?>) em.createQuery(query).getResultList();
    }

    @Override
    public List<ITrip> findByStatus(TripState state) {
        return (List<ITrip>) (List<?>) em.createNamedQuery(Constants.Q_TRIP_BY_STATE, Trip.class).setParameter("state", state).getResultList();
    }

    @Override
    public ITrip findById(Long id) {
        return em.find(Trip.class, id);
    }

    @Override
    public List<ITrip> findAll() {
        return (List<ITrip>) (List<?>) em.createQuery("select t from Trip t", Trip.class).getResultList();
    }
}
