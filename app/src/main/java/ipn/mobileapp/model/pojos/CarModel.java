package ipn.mobileapp.model.pojos;

import com.google.gson.Gson;

public class CarModel {
    private String carModelId;
    private String carImage;
    private String model;
    private String manufacturer;
    private int year;

    public CarModel() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(String carModelId) {
        this.carModelId = carModelId;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
