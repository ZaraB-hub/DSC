package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Match implements IMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @Embedded
    private Money fare;
    @OneToOne
    @JoinColumn(name=Constants.I_TRIP)
    private Trip trip;
    @ManyToOne
    @JoinColumn(name= Constants.I_VEHICLE)
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name=Constants.I_DRIVER)
    private Driver driver;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public IMoney getFare() {
        return fare;
    }

    @Override
    public void setFare(IMoney money) {
        this.fare = (Money) money;
    }

    @Override
    public ITrip getTrip() {
        return trip;
    }

    @Override
    public void setTrip(ITrip trip) {
        this.trip =  (Trip) trip;
    }

    @Override
    public IVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(IVehicle vehicle) {
        this.vehicle = (Vehicle) vehicle;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = (Driver) driver;
    }
}
