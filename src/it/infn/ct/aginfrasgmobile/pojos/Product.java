package it.infn.ct.aginfrasgmobile.pojos;

import java.io.Serializable;

public class Product implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int typeId;
	private int thumbId;
	private int file;
	private String procCentre;
	private String period;
	private String submissionDate;
	private String month;
	private int longMin;
	private int longMax;
	private int latMin;
	private int latMax;
	private byte[] thumbData;
	private String fileName;
	private int year;
	private String procSoft;
	private int description;
	private int instrShortName;	
	private String thumbURL;
	private String fileType;
	private String varLongName;
	private String stopUtc;
	private String startUtc;
	private String varShortName;
	private int day;
	private int level;
	private String platformName;
	private String keywords;
	private String lastModificationDate;
    private String size;
	
    
    
    public int getTypeId() {
		return typeId;
	}
	
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	public int getThumbId() {
		return thumbId;
	}
	
	public void setThumbId(int thumbId) {
		this.thumbId = thumbId;
	}
	
	public int getFile() {
		return file;
	}
	
	public void setFile(int file) {
		this.file = file;
	}
	
	public String getProcCentre() {
		return procCentre;
	}
	
	public void setProcCentre(String procCentre) {
		this.procCentre = procCentre;
	}
	
	public String getPeriod() {
		return period;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
	
	public String getSubmissionDate() {
		return submissionDate;
	}
	
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	
	public String getMonth() {
		return month;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
	
	public int getLongMin() {
		return longMin;
	}
	
	public void setLongMin(int longMin) {
		this.longMin = longMin;
	}
	
	public int getLongMax() {
		return longMax;
	}
	
	public void setLongMax(int longMax) {
		this.longMax = longMax;
	}
	
	public int getLatMin() {
		return latMin;
	}
	
	public void setLatMin(int latMin) {
		this.latMin = latMin;
	}
	
	public int getLatMax() {
		return latMax;
	}
	
	public void setLatMax(int latMax) {
		this.latMax = latMax;
	}
	
	public byte[] getThumbData() {
		return thumbData;
	}
	
	public void setThumbData(byte[] bs) {
		this.thumbData = bs;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getProcSoft() {
		return procSoft;
	}
	
	public void setProcSoft(String procSoft) {
		this.procSoft = procSoft;
	}
	
	public int getDescription() {
		return description;
	}
	
	public void setDescription(int description) {
		this.description = description;
	}
	
	public int getInstrShortName() {
		return instrShortName;
	}
	
	public void setInstrShortName(int instrShortName) {
		this.instrShortName = instrShortName;
	}
	
	public String getThumbURL() {
		return thumbURL;
	}
	
	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getVarLongName() {
		return varLongName;
	}
	
	public void setVarLongName(String varLongName) {
		this.varLongName = varLongName;
	}
	
	public String getStopUtc() {
		return stopUtc;
	}
	
	public void setStopUtc(String stopUtc) {
		this.stopUtc = stopUtc;
	}
	
	public String getStartUtc() {
		return startUtc;
	}
	
	public void setStartUtc(String startUtc) {
		this.startUtc = startUtc;
	}
	
	public String getVarShortName() {
		return varShortName;
	}
	
	public void setVarShortName(String varShortName) {
		this.varShortName = varShortName;
	}
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getPlatformName() {
		return platformName;
	}
	
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getLastModificationDate() {
		return lastModificationDate;
	}
	
	public void setLastModificationDate(String lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	
}
