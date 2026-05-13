package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripInfoDAO;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.impl.TripInfo;

import javax.persistence.EntityManager;
import java.util.List;

public class TripInfoDAO implements ITripInfoDAO {
    private final EntityManager em;

    public TripInfoDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public ITripInfo findById(Long id) {
        return em.find(TripInfo.class,id);
    }

    @Override
    public List<ITripInfo> findAll() {
        return List.of();
    }
}
