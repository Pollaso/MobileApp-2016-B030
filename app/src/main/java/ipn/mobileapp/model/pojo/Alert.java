package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

import java.sql.Date;

public class Alert {
    private String id;
    private String senderName;
    private String senderPhone;
    private Date dateSent;
    private double alcoholicState;
    private Coordinate coordinate;
    private String alertState;
    private String userId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public double getAlcoholicState() {
        return alcoholicState;
    }

    public void setAlcoholicState(double alcoholicState) {
        this.alcoholicState = alcoholicState;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getAlertState() {
        return alertState;
    }

    public void setAlertState(String alertState) {
        this.alertState = alertState;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
