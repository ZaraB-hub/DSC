package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.impl.Location;

import javax.persistence.EntityManager;
import java.util.List;

public class LocationDAO implements ILocationDAO {
    private final EntityManager em;

    public LocationDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public ILocation findById(Long id) {
        return em.find(Location.class,id);
    }

    @Override
    public List<ILocation> findAll() {
        return List.of();
    }
}
