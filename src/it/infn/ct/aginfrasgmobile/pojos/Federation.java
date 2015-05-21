package it.infn.ct.aginfrasgmobile.pojos;

import android.graphics.Bitmap;

public class Federation {

	private String name;
	private String country;
	private String flagURL;
	private Bitmap flag;
	private String logoURL;
	private Bitmap logo;
	private IDP[] idps;

	public Federation(){
	}
	
	public Federation(String name, IDP[] idps) {
		super();
		this.name = name;
		this.idps = idps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IDP[] getIdps() {
		return idps;
	}

	public void setIdps(IDP[] idps) {
		this.idps = idps;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Bitmap getFlag() {
		return flag;
	}

	public void setFlag(Bitmap flag) {
		this.flag = flag;
	}

	public String getFlagURL() {
		return flagURL;
	}

	public void setFlagURL(String flagURL) {
		this.flagURL = flagURL;
	}
	
	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Bitmap logo) {
		this.logo = logo;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	
	
}
