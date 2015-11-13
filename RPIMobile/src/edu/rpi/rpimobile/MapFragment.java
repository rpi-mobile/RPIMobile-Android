/**
 * Filename: MapFragment.java
 * Author: Peter Piech
 * Date: 3/15/2014
 * Description: MapFragment class creates the ListView
 *              from which the user selects from all of
 *              the RPI campus locations to be shown on
 *              a Google Map view.
 */

package edu.rpi.rpimobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.rpi.rpimobile.model.MapLocation;

public class MapFragment extends SherlockListFragment
/** Class used to implement the RPI campus map */
{
	
	private List<MapLocation> places; // Necessary for MapListAdapter
	private ArrayAdapter<MapLocation> adapter;
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
		if (places.size() == 0)
		{
			this.parseDatabase();
		}
		
		adapter = new ArrayAdapter<MapLocation>(getSherlockActivity(), R.layout.map_list_item, places);
		setListAdapter(adapter);
		
		return rootView;
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getSherlockActivity());
		if (statusCode == ConnectionResult.SUCCESS)
		{
			ViewMapFragment vmf = new ViewMapFragment();
			MapLocation tmpLoc = places.get(position);
			vmf.setMapParameters(tmpLoc.getName(), tmpLoc.getLatitude(), tmpLoc.getLongitude());
			FragmentTransaction ft = getSherlockActivity().getSupportFragmentManager().beginTransaction();
			ft.addToBackStack(null);
			ft.replace(R.id.content_frame, vmf);
			ft.commit();
		}
		else if (statusCode != ConnectionResult.SUCCESS)
		{
    		android.widget.Toast.makeText(getSherlockActivity(),
    				"Install the latest version of Google Play Services to use this feature",
    				Toast.LENGTH_LONG).show();
		}
	}
	
	private void parseDatabase() // TODO: Update numeric.xml value dbVersion every time you increment the versionCode in AndroidManifest.xml
	/** Manages accessing the database file which must be copied into
	 * internal storage (/data/data/.../) on first run because it can't
	 * be accessed directly from the /res/raw directory */
	{
		final String dbPathName = this.getActivity().getFilesDir().getPath() + "/content.db";
		SQLiteDatabase tmpDB;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		
		final int dbVersionInt = getResources().getInteger(R.integer.dbVersion);
		final int dbVersionPrefs = Integer.parseInt(prefs.getString("dbVersion", "0"));
		
		if (dbVersionPrefs < dbVersionInt) // i.e. the installed database is older than the one in the apk
		{
			final File oldDB = new File(dbPathName);
			oldDB.delete();
		}
		prefs.edit().putString("dbVersion", Integer.toString(dbVersionInt));
		
		try
		{
			// check to see if database already exists in internal storage
			tmpDB = SQLiteDatabase.openDatabase(dbPathName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLException e) // this will be thrown if the database is not present
		{
			try // copy the data from '/res/raw/map_locations.db' to internal storage
			{
				InputStream internalDB = this.getResources().openRawResource(R.raw.content);
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
				Toast.makeText(getSherlockActivity(), "Map failed. Please re-install app.", Toast.LENGTH_SHORT).show();
				throw new Error("Error copying database");
			}
		}
		try  // now that the data has been copied to internal storage, try again
		{
			tmpDB = SQLiteDatabase.openDatabase(dbPathName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLException e) // this should never be reached, or something is terribly wrong
		{
			Toast.makeText(getSherlockActivity(), "Map failed. Please re-install app.", Toast.LENGTH_SHORT).show();
			throw new Error("Error opening external database");
		}
		
		final String table = "locations";
		final String orderBy = "name ASC"; // name in ascending order
		final Cursor cursor = tmpDB.query(table, null, null, null, null, null, orderBy);
		
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
			this.places.add(new MapLocation(currName, currLati, currLong));		
		}
		
		cursor.close();
		tmpDB.close();
	}

	/*
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	// To be used when database is moved to server
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
	 // To be implemented when database is moved to server
	 {
		//logcat( "MapFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	//refresh the database
        	        	
        }
        
        return super.onOptionsItemSelected(item);
    }
	*/

}
