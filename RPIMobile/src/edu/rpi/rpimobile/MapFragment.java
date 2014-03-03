/**
 * Filename: MapFragment.java
 * Author: Peter Piech
 * Date: 12/4/2013
 * Description: MapFragment class creates the ListView
 *              from which the user selects from all of
 *              the RPI campus locations to be shown on
 *              a Google Map view.
 */

package edu.rpi.rpimobile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import edu.rpi.rpimobile.model.MapLocation;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class MapFragment extends SherlockFragment
/** Class used to implement the RPI campus map */
{
	
	private ArrayList<MapLocation> places; // Necessary for MapListAdapter
	private MapListAdapter listadapter; // Necessary for a ListView
	private SQLiteDatabase locations_db; // Stores all locations for ListView
	//private MenuItem refreshbutton; // To be used when locations database is moved to server
	
	public MapFragment()
	/** Class constructor, initializes the ArrayList places */
	{
		places = new ArrayList<MapLocation>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/** Upon being selected by the user, this method is called to create the ListView */
	{
		View rootView = inflater.inflate(R.layout.map_fragment, container, false);
		
		setHasOptionsMenu(true); // Options Menu is the "three-dots" button
		
		// Code below retrieves all data from an SQLite database file for the ListView
		try
		{
			locations_db = this.getDatabase();
		}
		catch (IOException e)
		{
			throw new Error("Unable to open database");
		}

		final String table = "locations";
		final String orderBy = "name ASC";
		final Cursor cursor = locations_db.query(table, null, null, null, null, null, orderBy);
		
		final String[] columns = {"name", "latitude", "longitude"};
		
		final int nameCol = cursor.getColumnIndexOrThrow(columns[0]);
		final int latiCol = cursor.getColumnIndexOrThrow(columns[1]);
		final int longCol = cursor.getColumnIndexOrThrow(columns[2]);
		
		while (cursor.moveToNext())
		// Populate the ArrayList with the data from the database file
		{
			String currName = cursor.getString(nameCol);
			double currLati = cursor.getDouble(latiCol);
			double currLong = cursor.getDouble(longCol);
			places.add(new MapLocation(currName, currLati, currLong));		
		}
		
		cursor.close();
		locations_db.close();

		ListView placesList = (ListView) rootView.findViewById(R.id.maplist);
		listadapter = new MapListAdapter(this.getActivity(), places, getSherlockActivity().getSupportFragmentManager());
		placesList.setAdapter(listadapter);
		
		return rootView;
	}
	
	private SQLiteDatabase getDatabase() throws SQLException, IOException
	/** Manages accessing the database file which must be copied into
	 * internal storage (/data/data/.../) on first run because it can't
	 * be accessed directly from the /res/raw directory */
	{
		final String dbPathName = this.getActivity().getFilesDir().getPath() + "/map_locations.db";
		SQLiteDatabase tmpDB;
		
		try
		{
			// check to see if database already exists in internal storage
			tmpDB = SQLiteDatabase.openDatabase(dbPathName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLException e) // this will be thrown if the database is not present
		{
			try // copy the data from '/res/raw/map_locations.db' to internal storage
			{
				InputStream internalDB = this.getResources().openRawResource(R.raw.map_locations);
				OutputStream externalDB =  new FileOutputStream(dbPathName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = internalDB.read(buffer))>0)
				{
					externalDB.write(buffer, 0, length);
				}
				externalDB.flush();
				externalDB.close();
				internalDB.close();
			}
			catch (IOException f)
			{
				throw new Error("Error copying database");
			}
		}
		try  // now that the data has been copied to internal storage, try again
		{
			tmpDB = SQLiteDatabase.openDatabase(dbPathName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLException e) // this should never be reached, or something is terribly wrong
		{
			throw new Error("Error opening external database");
		}
		return tmpDB;
	}

	/*
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	// To be used when locations database is moved to server
	{
		//Class called when the options menu is populated
		super.onCreateOptionsMenu(menu, inflater);
		//Add a refresh button and set its icon and visibility
		refreshbutton = menu.add("Refresh");
		refreshbutton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		refreshbutton.setIcon(R.drawable.navigation_refresh);
	}
	*/
	
	/*
	 public boolean onOptionsItemSelected(MenuItem item)
	 // To be implemented when locations database is moved to server
	 {
		//logcat( "EventsFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	//refresh the database
        	        	
        }
        
        return super.onOptionsItemSelected(item);
    }
	*/
	
	

}
