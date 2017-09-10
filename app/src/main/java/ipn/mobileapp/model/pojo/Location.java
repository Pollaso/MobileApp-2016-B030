package ipn.mobileapp.model.pojo;

import com.google.gson.Gson;

public class Location {
	private String locationId;
	private String division;
	private String state;
	private String country;

	public Location() {
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
