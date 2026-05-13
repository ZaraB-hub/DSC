package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.IVehicle;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@NamedQuery(name= Constants.Q_ORGANIZATION_BY_VEHICLE_DRIVE_RATING,query = "select o from Organization o join o.employments e join e.id.driver  d join d.vehicle v where e.active=:active and v.type=:type and d.avgRating>:minRating")
public class Organization implements IOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = Constants.J_ORGANIZATION_PARTS,
            joinColumns = @JoinColumn(name = Constants.I_ORGANIZATION_PARTS),
            inverseJoinColumns = @JoinColumn(name = Constants.I_ORGANIZATION_PART_OF)
    )
    private Collection<Organization> parts = new ArrayList<>();

    @ManyToMany(mappedBy = "parts")
    private Collection<Organization> partOf = new ArrayList<>();

    @OneToMany(mappedBy = "id.organization")
    private Collection<Employment> employments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = Constants.J_ORGANIZATION_VEHICLE,
            joinColumns = @JoinColumn(name = Constants.I_ORGANIZATION),
            inverseJoinColumns = @JoinColumn(name = Constants.I_VEHICLES)
    )
    private Collection<Vehicle> vehicles = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<IOrganization> getParts() {
        return (Collection<IOrganization>) (Collection<?>) parts;
    }

    @Override
    public void setParts(Collection<IOrganization> parts) {
        this.parts = (Collection<Organization>) (Collection<?>) parts;
    }

    @Override
    public void addPart(IOrganization part) {
        parts.add( (Organization)part);
    }

    @Override
    public Collection<IOrganization> getPartOf() {
        return (Collection<IOrganization>) (Collection<?>) partOf;
    }

    @Override
    public void setPartOf(Collection<IOrganization> partOf) {
        this.partOf = (Collection<Organization>) (Collection<?>)partOf;
    }

    @Override
    public void addPartOf(IOrganization partOf) {
        this.partOf.add((Organization) partOf);
    }

    @Override
    public Collection<IEmployment> getEmployments() {
        return (Collection<IEmployment>) (Collection<?>) employments;
    }

    @Override
    public void setEmployments(Collection<IEmployment> employments) {
        this.employments = (Collection<Employment>) (Collection<?>) employments;}

    @Override
    public void addEmployment(IEmployment employment) {
        this.employments.add( (Employment) employment);
    }

    @Override
    public Collection<IVehicle> getVehicles() {
        return (Collection<IVehicle>) (Collection<?>)  vehicles;
    }

    @Override
    public void setVehicles(Collection<IVehicle> vehicles) {
        this.vehicles = (Collection<Vehicle>) (Collection<?>) vehicles;
    }

    @Override
    public void addVehicle(IVehicle vehicle) {
        this.vehicles.add((Vehicle) vehicle);
    }
}
