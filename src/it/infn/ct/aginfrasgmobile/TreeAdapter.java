package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.pojos.TreeElement;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TreeAdapter extends ArrayAdapter<TreeElement> {

	Context context;

	public TreeAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
	}

	public void setData(List<TreeElement> data) {
        clear();
        if (data != null) {
            for (TreeElement p : data) {
                add(p);
            }
        }
    }
	
	private class ViewHolder {
        ImageView imageView;
        TextView text;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TreeElement element = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.type_lv_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.folderImage);

            convertView.setTag(holder);
//        } else
//            holder = (ViewHolder) convertView.getTag();
 
        holder.text.setText(element.getType().getText());
        holder.imageView.setImageResource(R.drawable.ic_folder);

        if(element.getPadding()!=0 && position!=0)
        	holder.imageView.setPadding(element.getPadding(), 0, 0, 0);
       
        return convertView;
    }
	
}
