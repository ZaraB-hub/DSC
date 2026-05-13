package dst.ass1.jooq.model.impl;

import dst.ass1.jooq.model.IRiderPreference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RiderPreference implements IRiderPreference {
    private Long riderId;
    private String vehicleClass;
    private String area;
    private Map<String, String> preferences = new HashMap<>();

    @Override
    public String getArea() {
        return this.area;
    }

    @Override
    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String getVehicleClass() {
        return this.vehicleClass;
    }

    @Override
    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    @Override
    public Long getRiderId() {
        return riderId;
    }

    @Override
    public void setRiderId(Long personId) {
        this.riderId = personId;
    }

    @Override
    public Map<String, String> getPreferences() {
        return preferences;
    }

    @Override
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RiderPreference other = (RiderPreference) obj;
        return Objects.equals(riderId, other.riderId) &&
                Objects.equals(area, other.area) &&
                Objects.equals(vehicleClass, other.vehicleClass) &&
                Objects.equals(preferences, other.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(riderId, area, vehicleClass, preferences);
    }
}
