package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;

import android.graphics.Bitmap;

public class IDP implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private String origin;
	private String country;
	private String flagURL;
	private transient Bitmap flag;
	private String logoURL;
	private transient Bitmap logo;
	
	
	public IDP() {
		super();
	
	}
	
	public IDP(String displayName, String origin){
		this.displayName = displayName;
		this.origin 	 = origin;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFlagURL() {
		return flagURL;
	}

	public void setFlagURL(String flagURL) {
		this.flagURL = flagURL;
	}

	public Bitmap getFlag() {
		return flag;
	}

	public void setFlag(Bitmap flag) {
		this.flag = flag;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Bitmap logo) {
		this.logo = logo;
	}
	
	
}
