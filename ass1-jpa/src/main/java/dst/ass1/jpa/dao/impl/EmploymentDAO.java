package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IEmploymentDAO;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.impl.Employment;

import javax.persistence.EntityManager;
import java.util.List;

public class EmploymentDAO implements IEmploymentDAO {
    private final EntityManager em;

    public EmploymentDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public IEmployment findById(Long id) {
        return em.find(Employment.class,id);
    }

    @Override
    public List<IEmployment> findAll() {
        return List.of();
    }
}
