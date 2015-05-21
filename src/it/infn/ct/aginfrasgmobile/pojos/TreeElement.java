package it.infn.ct.aginfrasgmobile.pojos;

public class TreeElement {
	
	private Type type;
	private int padding;
	
	
	
	public TreeElement(Type t, int pad) {
		this.type = t;
		this.padding = pad;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type t) {
		this.type = t;
	}
	
	public int getPadding() {
		return padding;
	}
	
	public void setPadding(int padding) {
		this.padding = padding;
	}

}
