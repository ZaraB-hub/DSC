package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@NamedQuery(name = Constants.Q_TRIP_BY_STATE, query = "Select t from Trip t where t.state = :state")
@NamedQuery(name = Constants.Q_TRIP_COMPLETED_MIN_STOPS, query = "SELECT t FROM Trip t WHERE t.state = :state AND SIZE(t.stops) > :minStops"
)
public class Trip implements ITrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private TripState state;
    @ManyToOne
    @JoinColumn(name = Constants.I_PICKUP, nullable = false)
    private Location pickup;
    @ManyToOne
    @JoinColumn(name = Constants.I_DESTINATION, nullable = false)
    private Location destination;
    @ManyToMany
    @JoinTable(
            name = Constants.J_TRIP_LOCATION,
            joinColumns = @JoinColumn(name = Constants.I_TRIP),
            inverseJoinColumns = @JoinColumn(name = Constants.I_STOPS)
    )
    private Collection<Location> stops = new ArrayList<>();
    @OneToOne(mappedBy ="trip" )
    private TripInfo tripInfo;
    @OneToOne(mappedBy = "trip")
    private Match match;
    @ManyToOne
    @JoinColumn(name = Constants.I_RIDER, nullable = true)
    private Rider rider;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public TripState getState() {
        return state;
    }

    @Override
    public void setState(TripState state) {
        this.state = state;
    }

    @Override
    public ILocation getPickup() {
        return pickup;
    }

    @Override
    public void setPickup(ILocation pickup) {
        this.pickup = (Location) pickup;
    }

    @Override
    public ILocation getDestination() {
        return destination;
    }

    @Override
    public void setDestination(ILocation destination) {
        this.destination = (Location) destination;
    }

    @Override
    public Collection<ILocation> getStops() {
        return (Collection<ILocation>) (Collection<?>) stops;
    }

    @Override
    public void setStops(Collection<ILocation> stops) {
        this.stops =  (Collection<Location>) (Collection<?>) stops;
    }

    @Override
    public void addStop(ILocation stop) {
       stops.add((Location) stop);
    }

    @Override
    public ITripInfo getTripInfo() {
        return tripInfo;
    }

    @Override
    public void setTripInfo(ITripInfo tripInfo) {
        this.tripInfo = (TripInfo) tripInfo;
    }

    @Override
    public IMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(IMatch match) {
        this.match = (Match) match;
    }

    @Override
    public IRider getRider() {
        return rider;
    }

    @Override
    public void setRider(IRider rider) {
        this.rider = (Rider) rider;
    }
}
