package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@NamedQuery(name = Constants.Q_DRIVER_HIGHLY_RATED_ACTIVE, query = "SELECT DISTINCT  d from Driver d JOIN d.employments e WHERE d.avgRating >= :minRating AND e.active = true ORDER BY d.avgRating DESC")
@NamedQuery(name = Constants.Q_DRIVER_TOP_PERFORMING, query = "SELECT DISTINCT d FROM Driver d WHERE d IN (SELECT m.driver FROM Match m JOIN m.trip t JOIN t.tripInfo ti WHERE ti.completed >= :startDate AND ti.completed <= :endDate AND ti.driverRating > 5 GROUP BY m.driver HAVING COUNT(t) >= :minTrips) AND d NOT IN (SELECT m2.driver FROM Match m2 JOIN m2.trip t2 JOIN t2.tripInfo ti2 WHERE ti2.driverRating < 3)")
public class Driver extends PlatformUser implements IDriver {
    @ManyToOne
    @JoinColumn(name = Constants.I_VEHICLE,nullable = false)
    private Vehicle vehicle;
    @OneToMany(mappedBy = "id.driver")
    private Collection<Employment> employments = new ArrayList<>();

    @Override
    public Collection<IEmployment> getEmployments() {
        return (Collection<IEmployment>) (Collection<?>) employments;
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments = (Collection<Employment>) (Collection<?>) employments;
    }

    @Override
    public void addEmployment(IEmployment employment) {
        employments.add((Employment) employment);
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = (Vehicle) vehicle;
    }
}
