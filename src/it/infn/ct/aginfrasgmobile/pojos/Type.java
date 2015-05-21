package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Type implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text;
	private boolean leaf;
	private int id;
	private String path;
	private ArrayList<Type> leafs;
	
	
	public Type(String text, boolean leaf, int id,
			String path) {
		super();
		this.text = text;
		this.leaf = leaf;
		this.id = id;
		this.path = path;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isLeaf() {
		return leaf;
	}
	
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return text;
	}

	public ArrayList<Type> getLeafs() {
		return leafs;
	}

	public void setLeafs(ArrayList<Type> leafes) {
		this.leafs = leafes;
	}
	
}
