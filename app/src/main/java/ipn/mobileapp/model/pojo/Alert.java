package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class Alert {
    public static final int NEW = 0;
    public static final int PENDING = 1;
    public static final int ANSWERED = 2;

    private String id;
    private String senderName;
    private String senderPhone;
    private Date dateSent;
    private int alcoholicState;
    private Coordinate coordinate;
    private int alertState;
    private String userId;

    public Alert(AlcoholTest alcoholTest, User user) {
        this.senderName = user.getName() + " " + user.getPaternalSurname() + " " + user.getMaternalSurname();
        this.senderPhone = user.getPhoneNumber();
        this.dateSent = alcoholTest.getOcurrence();
        this.alcoholicState = alcoholTest.getAlcoholicState();
        this.coordinate = alcoholTest.getCoordinate();
        this.alertState = 0;
        this.userId = user.getUserId();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create().toJson(this);
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

    public void setAlcoholicState(int alcoholicState) {
        this.alcoholicState = alcoholicState;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getAlertState() {
        return alertState;
    }

    public void setAlertState(int alertState) {
        this.alertState = alertState;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
