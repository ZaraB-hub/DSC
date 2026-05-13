package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPaymentInfo;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.util.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "name"})
)
public class Rider extends PlatformUser implements IRider {
    @Column(unique = true,nullable = false) // TODO check
    private String email;
    @Column(length = 20, columnDefinition = "VARBINARY(20)")
    private byte[] password;
    @OneToMany
    @JoinTable(
            name = Constants.J_RIDER_PAYMENT_INFO,
            joinColumns = @JoinColumn(name = Constants.I_RIDER),
            inverseJoinColumns = @JoinColumn(name = Constants.I_PAYMENT_INFOS)
    )
    private Collection<PaymentInfo> paymentInfos = new ArrayList<>();

    @OneToMany(mappedBy = "rider")
    private Collection<Trip> trips = new ArrayList<>();

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public Collection<ITrip> getTrips() {
        return (Collection<ITrip>) (Collection<?>) trips;
    }

    @Override
    public void setTrips(Collection<ITrip> trips) {
        this.trips = (Collection<Trip>) (Collection<?>) trips;
    }

    @Override
    public void addTrip(ITrip trip) {
        trips.add( (Trip) trip);
    }

    @Override
    public Collection<IPaymentInfo> getPaymentInfos() {
        return (Collection<IPaymentInfo>) (Collection<?>) paymentInfos;
    }

    @Override
    public void setPaymentInfos(Collection<IPaymentInfo> paymentInfos) {
        this.paymentInfos = (Collection<PaymentInfo>) (Collection<?>) paymentInfos;
    }
}
