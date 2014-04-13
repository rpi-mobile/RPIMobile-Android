/**
 * Filename: ShuttlesRoutes.java
 * Author: Peter Piech
 * Date: 4/12/2014
 * Description: ShuttlesRoutes is a basic class
 *				that holds intermediate data
 *				to be used in the GoogleMap
 *				due to the limitation of not
 *				being able to write to the
 *				GoogleMap UI element from
 *				the AsyncTask class directly.
 */

package edu.rpi.rpimobile.model;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShuttlesRoutes
{
	private ArrayList<PolylineOptions> polylines;
	private ArrayList<MarkerOptions> stops;
	
	public ShuttlesRoutes()
	{
		polylines = new ArrayList<PolylineOptions>();
		stops = new ArrayList<MarkerOptions>();
	}
	
	public void add(PolylineOptions item)
	{
		polylines.add(item);
	}
	
	public void add(MarkerOptions item)
	{
		stops.add(item);
	}
	
	public void publishTo(GoogleMap g_map)
	{
		for (PolylineOptions po : polylines)
		{
			g_map.addPolyline(po);
		}
		for (MarkerOptions mo : stops)
		{
			g_map.addMarker(mo);
		}
	}

}
