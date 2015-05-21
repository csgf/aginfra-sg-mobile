package it.infn.ct.aginfrasgmobile.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Column implements Parcelable {

	private String header;
	private boolean sortable;
	private String dataIndex;
	private int width;
	private String colType;
	private String align;
	private boolean hidden;
	private String css;
	private String id;

	public Column() {
		super();
	}

	public Column(String header, boolean sortable, String dataIndex, int width,
			String colType, String align, boolean hidden, String css, String id) {
		super();
		this.header = header;
		this.sortable = sortable;
		this.dataIndex = dataIndex;
		this.width = width;
		this.colType = colType;
		this.align = align;
		this.setHidden(hidden);
		this.css = css;
		this.id = id;
	}

	public Column(String header, boolean sortable, String dataIndex, int width,
			String colType, String align, boolean hidden, String css) {
		this(header, sortable, dataIndex, width, colType, align, hidden, css,
				"");
	}

	// Parcelling part
    public Column(Parcel in){
        String[]  stringData = new String[6];
        boolean[] boolData 	 = new boolean[2];
                
        in.readStringArray(stringData);
        in.readBooleanArray(boolData);
        this.width=in.readInt();
        
        this.align   	= stringData[0];
        this.colType 	= stringData[1];
		this.css		= stringData[2];
		this.dataIndex	= stringData[3];
		this.header		= stringData[4];
		this.id			= stringData[5];
        
		this.hidden		= boolData[0];
		this.sortable	= boolData[1];
    }
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean getSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeStringArray(new String[] { this.align, this.colType,
				this.css, this.dataIndex, this.header, this.id

		});
		dest.writeBooleanArray(new boolean[] { this.hidden, this.sortable });
		dest.writeInt(this.width);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Column createFromParcel(Parcel in) {
			return new Column(in);
		}

		public Column[] newArray(int size) {
			return new Column[size];
		}
	};

}
