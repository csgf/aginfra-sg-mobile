package it.infn.ct.aginfrasgmobile.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Replica implements Parcelable {

	private double lat;
	private double lng;
	private int enabled;
	private String link;
	private String name;

	public Replica(double lat, double lng, Object enabled, String link,
			String name) {
		super();
		this.lat = lat;
		this.lng = lng;
		if (enabled instanceof Boolean) {
			Boolean tmpEnabled = (Boolean) enabled;
			if (tmpEnabled)
				this.enabled = 1;
			else
				this.enabled = 0;
		} else if (enabled instanceof String)
			this.enabled = Integer.parseInt(enabled.toString());

		setLink(link);
		this.name = name;
	}

	public Replica(double lat, double lng, int enabled, String link, String name) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.enabled = enabled;
		setLink(link);
		this.name = name;
	}

	public Replica() {
		super();
	}

	public Replica(Parcel in) {

		String[] stringData = new String[2];
		double[] doubleData = new double[2];

		in.readDoubleArray(doubleData);
		in.readStringArray(stringData);

		this.enabled = in.readInt();

		this.link = stringData[0];
		this.name = stringData[1];

		this.lat = doubleData[0];
		this.lng = doubleData[1];
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
//		String tmp = link.replace("<a href=", "");
//		tmp = tmp.substring(0, tmp.indexOf(" "));
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(new double[] { this.lat, this.lng });
		dest.writeStringArray(new String[] { this.link, this.name });
		dest.writeInt(this.enabled);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Replica createFromParcel(Parcel in) {
			return new Replica(in);
		}

		public Replica[] newArray(int size) {
			return new Replica[size];
		}
	};
}
