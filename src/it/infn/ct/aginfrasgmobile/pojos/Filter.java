package it.infn.ct.aginfrasgmobile.pojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable{

	private String[] filterList;
	private String labelField;
	private String type;
	private String dataIndex;
	
	public Filter(Parcel in) {
		List<String> filtersListData = new ArrayList<String>();
		String[] stringData = new String[3];
		
		in.readStringList(filtersListData);
		in.readStringArray(stringData);
		
		setFilterList(filtersListData.toArray(new String[filtersListData.size()]));
		setDataindex(stringData[0]);
		setLabelField(stringData[1]);
		setType(stringData[2]);
	}

	public Filter() {
		
	}

	public Filter(String dataIndex, String filterValue) {

		setDataindex(dataIndex);
		setFilterList(new String[]{filterValue});
	}

	public String[] getFilterList() {
		if(this.filterList != null)
			return filterList;
		else
			return new String[0];
	}
	
	public void setFilterList(String[] filterList) {
		this.filterList = filterList;
	}

	public String getLabelField() {
		return labelField;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDataindex() {
		return dataIndex;
	}

	public void setDataindex(String dataindex) {
		this.dataIndex = dataindex;
	}

	@Override
	public String toString() {
		return "Filter [filterList=" + Arrays.toString(filterList)
				+ ", labelField=" + labelField + ", type=" + type
				+ ", dataindex=" + dataIndex + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		List<String> tmp = new ArrayList<String>();
		for (String string : getFilterList()) {
			tmp.add(string);
		}
		dest.writeStringList(tmp);
//		dest.writeStringArray(this.filterList);
		dest.writeStringArray(new String[]{this.dataIndex, this.labelField, this.type});
				
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Filter createFromParcel(Parcel in) {
			return new Filter(in);
		}

		public Filter[] newArray(int size) {
			return new Filter[size];
		}
	};

	public void addFilterValue(String string) {
		String[] oldFilterList = getFilterList();
		String[] newFilterList;
		if(getFilterList()!=null){
			newFilterList = new String[getFilterList().length+1];

			for (int i = 0; i < oldFilterList.length; i++) {
				newFilterList[i] = oldFilterList[i];
			}
			newFilterList[oldFilterList.length] = string;
			
		} else {
			newFilterList = new String[1];
			newFilterList[0] = string;
		}
			
		setFilterList(newFilterList);
	}

	public String[] removefilterValue(String removingValue) {
		String[] oldFilterList = getFilterList();
		ArrayList<String> newFilterList = null;
		if(oldFilterList != null)
			if(oldFilterList.length > 1){
				newFilterList = new ArrayList<String>();
				for (int i = 0; i < oldFilterList.length; i++) {
					if(!oldFilterList[i].equals(removingValue))
						newFilterList.add(oldFilterList[i]);
				}
				oldFilterList = new String[newFilterList.size()];
				newFilterList.toArray(oldFilterList);
				setFilterList(oldFilterList);

			} else
				setFilterList(null);
	
		return getFilterList();
	}
	
}
