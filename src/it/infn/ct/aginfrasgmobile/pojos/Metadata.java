package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Metadata implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String totalProperty;
	private ArrayList<Field> fields;
	private String root;
	private String custom;
	
	public String getTotalProperty() {
		return totalProperty;
	}
	
	public void setTotalProperty(String totalProperty) {
		this.totalProperty = totalProperty;
	}
	
	public ArrayList<Field> getFields() {
		return fields;
	}
	
	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}
	
	public String getRoot() {
		return root;
	}
	
	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getCustom() {
		return custom;
	}
	
	public void setCustom(String custom) {
		this.custom = custom;
	}
	
}
