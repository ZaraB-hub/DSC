package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.impl.Driver;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class DriverDAO implements IDriverDAO {
    private final EntityManager em;

    public DriverDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<IDriver> findActiveHighlyRatedDrivers(double minRating) {
        if (minRating < 0) {
            throw new IllegalArgumentException("Rating cannot be negative");
        }
        return (List<IDriver>) (List<?>) em.createNamedQuery(Constants.Q_DRIVER_HIGHLY_RATED_ACTIVE, Driver.class)
                .setParameter("minRating", minRating)
                .getResultList();
    }

    @Override
    public List<IDriver> findTopPerformingDrivers(Long minTrips, Date startDate, Date endDate) {
        if (minTrips < 0 || endDate.before(startDate)) {
            throw new IllegalArgumentException();
        }
        return (List<IDriver>) (List<?>) em.createNamedQuery(Constants.Q_DRIVER_TOP_PERFORMING, Driver.class)
                .setParameter("minTrips", minTrips)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .getResultList();
    }

    @Override
    public IDriver findById(Long id) {
        return em.find(Driver.class,id);
    }

    @Override
    public List<IDriver> findAll() {
        return List.of();
    }
}
