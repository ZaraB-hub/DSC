package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;

import javax.persistence.EntityManager;
import java.util.List;

public class RiderDAO implements IRiderDAO {
    private final EntityManager em;

    public RiderDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public IRider findByEmail(String email) {
        List<Rider> results = em.createQuery(
                        "select r from Rider r where r.email = :email", Rider.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public IRider findById(Long id) {
        return em.find(Rider.class,id);
    }

    @Override
    public List<IRider> findAll() {
        return List.of();
    }
}
