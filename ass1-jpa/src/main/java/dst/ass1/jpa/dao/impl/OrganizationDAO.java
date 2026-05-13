package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.impl.Organization;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import java.util.List;

public class OrganizationDAO implements IOrganizationDAO {
    private final EntityManager em;

    public OrganizationDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<IOrganization> findOrganizationsByVehicleTypeAndDriverRating(String vehicleType, double minRating) {
        if(minRating<0){
            throw new IllegalArgumentException();
        }
        return (List<IOrganization>) (List<?>) em.createNamedQuery(Constants.Q_ORGANIZATION_BY_VEHICLE_DRIVE_RATING,Organization.class).setParameter("active",true).setParameter("minRating",minRating).setParameter("type",vehicleType).getResultList();
    }

    @Override
    public IOrganization findById(Long id) {
        return em.find(Organization.class,id);
    }

    @Override
    public List<IOrganization> findAll() {
        return (List<IOrganization>) (List<?>) em.createQuery("select o from Organization o ",Organization.class).getResultList() ;
    }
}
