package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.ITripInfo;
import dst.ass1.jpa.model.ITripReceipt;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TripInfo implements ITripInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date completed;
    private Double distance;
    private int driverRating;
    private int riderRating;
    @OneToOne
    @JoinColumn(name=Constants.I_TRIP,nullable=false)
    private Trip trip;
    @OneToOne
    @JoinColumn(name= Constants.I_TRIP_RECEIPT)
    private TripReceipt receipt;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(Date date) {
        this.completed = date;
    }

    @Override
    public Double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public Integer getDriverRating() {
        return driverRating;
    }

    @Override
    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    @Override
    public Integer getRiderRating() {
        return riderRating;
    }

    @Override
    public void setRiderRating(Integer riderRating) {
        this.riderRating = riderRating;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip = (Trip) trip;
    }

    @Override
    public ITripReceipt getReceipt() {
        return receipt;
    }

    @Override
    public void setReceipt(ITripReceipt receipt) {
        this.receipt = (TripReceipt) receipt;
    }
}
