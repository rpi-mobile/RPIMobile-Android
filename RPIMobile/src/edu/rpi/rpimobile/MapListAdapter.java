/**
 * Filename: MapListAdapter.java
 * Author: Peter Piech
 * Date: 12/1/2013
 * Description: MapListAdapter class draws the ListView
 *              from the MapFragment class.
 */

package edu.rpi.rpimobile;

import java.util.ArrayList;

import edu.rpi.rpimobile.model.MapLocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MapListAdapter extends BaseAdapter
/** Class used to draw the ListView of places on campus. Necessary. */
{
	
	private Context context;
	private ArrayList<MapLocation> places;
	private LayoutInflater inflater;
	
	public MapListAdapter(Context context_, ArrayList<MapLocation> places_)
	{
		this.context = context_;
		this.places = places_;
	}

	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.map_list_item, parent, false);
		
		TextView tvName = (TextView) itemView.findViewById(R.id.placename);
		tvName.setText(places.get(index).getName());
		
		itemView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent mapIntent = new Intent();
				//mapIntent.addExtra(); // name, lat, & long info
				//context.startActivity(mapIntent);
			}
		});
				
		return itemView;
		
	}

}
