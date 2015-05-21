package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;
import java.util.HashMap;

public class Field implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> field ;

	public HashMap<String, String> getField() {
		return field;
	}

	public void setField(HashMap<String, String> field) {
		this.field = field;
	}

}
