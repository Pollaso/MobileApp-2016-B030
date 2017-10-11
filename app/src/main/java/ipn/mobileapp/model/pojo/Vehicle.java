package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

public class Vehicle {
    private String id;
    private String licensePlate;
    private String owner;
    private String user;
    private Device device;

    public Vehicle() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

