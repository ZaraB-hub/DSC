package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IEmploymentKey;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.util.Constants;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmploymentKey implements IEmploymentKey, Serializable {
    @ManyToOne
    @JoinColumn(name = Constants.I_DRIVER)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = Constants.I_ORGANIZATION)
    private Organization organization;

    public EmploymentKey(IDriver driver, IOrganization organization) {
        this.driver = (Driver) driver;
        this.organization = (Organization) organization;
    }

    public EmploymentKey() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmploymentKey that = (EmploymentKey) o;
        return Objects.equals(driver, that.driver) &&
                Objects.equals(organization, that.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driver, organization);
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = (Driver)driver;
    }

    @Override
    public IOrganization getOrganization() {
        return organization;
    }

    @Override
    public void setOrganization(IOrganization organization) {
        this.organization = (Organization)  organization;
    }
}
