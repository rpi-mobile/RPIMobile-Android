/**
 * Filename: TVGuideFragment.java
 * Author: Peter Piech
 * Date: 3/15/2014
 * Description: TVGuideFragment class creates the ListView
 *              from which the user selects from all of
 *              the RPI campus television stations to be
 *              taken to their corresponding website.
 */

package edu.rpi.rpimobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import edu.rpi.rpimobile.model.TVChannel;

public class TVGuideFragment extends SherlockFragment
/** Class used to implement the TV Guide feature */
{
	private ArrayList<TVChannel> channels;
	private TVGuideListAdapter listadapter;
	
	public TVGuideFragment()
	{
		channels = new ArrayList<TVChannel>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/** Upon being selected by the user, this method is called to create the ListView */
	{
		View rootView = inflater.inflate(R.layout.tvguide_fragment, container, false);
		setHasOptionsMenu(true); // Options Menu is the "three-dots" button
		
		// Code below retrieves all data from an SQLite database file for the ListView
		if (channels.size() == 0)
		{
			Log.d("RPI", "About to parse database");
			this.parseDatabase();
			Log.d("RPI", "Parsed database");
		}
		
		Log.d("RPI", "About to call findViewById()");
		ListView channelsList = (ListView) rootView.findViewById(R.id.channelList);
		listadapter = new TVGuideListAdapter(this.getSherlockActivity(), channels);
		channelsList.setAdapter(listadapter);
		
		return rootView;
	}
	
	private void parseDatabase() // TODO: Update strings.xml value dbTimestamp every time you edit the database
	/** Manages accessing the database file which must be copied into
	 * internal storage (/data/data/.../) on first run because it can't
	 * be accessed directly from the /res/raw directory */
	{
		final String dbPathName = this.getActivity().getFilesDir().getPath() + "/content.db";
		SQLiteDatabase tmpDB;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		
		final DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		final String dbTimestampStr = (String) getResources().getText(R.string.dbTimestamp);
		
		Date dbInstallDate = null;
		Date dbTimestamp = null;
		
		try {
			dbInstallDate = df.parse(prefs.getString("dbInstallDate", dbTimestampStr));
			dbTimestamp = df.parse(dbTimestampStr);
		} catch (ParseException e) {
			// If this is caught: you have incorrectly formatted
			// the dbTimestamp string in the strings.xml file
			Log.d("RPI", "ParseException in TVGuideFragment. Incorrect dbTimestamp format in strings.xml");
		}
		
		if (dbTimestamp.after(dbInstallDate))
		{
			final File oldDB = new File(dbPathName);
			oldDB.delete();
		}
		prefs.edit().putString("dbInstallDate", df.format(new Date())).commit();
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
		
		final String table = "channels";
		final String orderBy = "_id ASC"; // network in ascending order
		final Cursor cursor = tmpDB.query(table, null, null, null, null, null, orderBy);
		
		final String[] columns = {"network", "number", "url"};
		
		final int networkCol = cursor.getColumnIndexOrThrow(columns[0]);
		final int numberCol = cursor.getColumnIndexOrThrow(columns[1]);
		final int urlCol = cursor.getColumnIndexOrThrow(columns[2]);
		
		while (cursor.moveToNext())
		// Populate the ArrayList with the data from the database file
		{
			String currNetwork = cursor.getString(networkCol);
			String currNumber = cursor.getString(numberCol);
			String currURL = cursor.getString(urlCol);
			this.channels.add(new TVChannel(currNetwork, currNumber, currURL));
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
		//logcat( "TVGuideFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	//refresh the database
        	        	
        }
        
        return super.onOptionsItemSelected(item);
    }
	*/
}