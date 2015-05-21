package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;
import java.util.HashMap;

public class NewProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Object[]> entry;

	public NewProduct() {
		super();
		this.entry = new HashMap<String, Object[]>();
	}

	public HashMap<String, Object[]> getEntry() {
		return entry;
	}

	public Object[] getEntry(String name) {
		return entry.get(name);
	}

	public void setEntry(String name, Object[] entry) {
		this.entry.put(name, entry);
	}
	
}
