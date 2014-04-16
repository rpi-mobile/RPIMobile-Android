package edu.rpi.rpimobile;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.rpi.rpimobile.model.Building;
 
public class LaundryListAdapter extends BaseAdapter {
 
	//All variables to be used throughout the function
    Context context;
    ArrayList<Building> buildings;
    LayoutInflater inflater;
 
    public LaundryListAdapter(Context context, ArrayList<Building> buildings_) {
    	//Assign passed list and context to local variables in the class 
        this.context = context;
        this.buildings = buildings_;

    }
 
    @Override
    public int getCount() {
    	//Method to tell Android the amount of items in the list
        return buildings.size();
    }
 
  //These functions are not used in the current implementation
    @Override
    public Object getItem(int position) {
        return buildings.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View convertView, ViewGroup parent) {
    	//inflate the layout into the parent view
    	Collections.sort(buildings);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.laundry_list_item, parent,
                false);
     
        // Declare Views and assign them to their respective widgets defined in the xml file
        TextView txttitle = (TextView) itemView.findViewById(R.id.building_title);
        TextView txtavai_washers = (TextView) itemView.findViewById(R.id.available_washers);
        TextView txtavai_dryers = (TextView) itemView.findViewById(R.id.available_dryers);
        TextView txtused_washers = (TextView) itemView.findViewById(R.id.used_washers);
        TextView txtused_dryers = (TextView) itemView.findViewById(R.id.used_dryers);
        
        
        //Onclick listener to eventually be used to open each room's individual laundry status
        /*itemView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				
			}
		});//*/
        
        
        // Set the results into TextViews
        txttitle.setText(buildings.get(position).getTag());
        txtavai_washers.setText(String.valueOf(buildings.get(position).getAvailableWashers()));
        txtavai_dryers.setText(String.valueOf(buildings.get(position).getAvailableDryers()));
        txtused_washers.setText(String.valueOf(buildings.get(position).getUsedWashers()));
        txtused_dryers.setText(String.valueOf(buildings.get(position).getUsedDryers()));
        
        return itemView;
    }
 
}