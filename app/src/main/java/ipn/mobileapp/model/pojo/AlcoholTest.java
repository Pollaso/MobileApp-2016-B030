package ipn.mobileapp.model.pojo;

import com.google.gson.GsonBuilder;

import java.sql.Date;

public class AlcoholTest {
	private Date ocurrence;
	private Coordinate coordinate;
	private int alcoholicState;
	private Location location;
	private String userId;

	@Override
	public String toString() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create().toJson(this);
	}

	public Date getOcurrence() {
		return ocurrence;
	}

	public void setOcurrence(Date ocurrence) {
		this.ocurrence = ocurrence;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public int getAlcoholicState() {
		return alcoholicState;
	}

	public void setAlcoholicState(int alcoholicState) {
		this.alcoholicState = alcoholicState;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}