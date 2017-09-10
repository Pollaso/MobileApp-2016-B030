package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

import java.sql.Date;

public class BACTest {
	private Date ocurrence;
	private Coordinate coordinate;
	private double alcoholicState;
	private Location location;
	private String userId;

	@Override
	public String toString() {
		return new Gson().toJson(this);
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

	public double getAlcoholicState() {
		return alcoholicState;
	}

	public void setAlcoholicState(double alcoholicState) {
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