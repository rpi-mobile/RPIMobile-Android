package edu.rpi.rpimobile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.rpi.rpimobile.model.CalendarEvent;
import edu.rpi.rpimobile.util.Util;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

//Events Calendar fragment
public class EventsFragment extends SherlockFragment
{
    
	//All variables to be used throughout the function
	private JSONObject jObj;
	private ArrayList<CalendarEvent> events;
	private EventsListAdapter listadapter;
	private MenuItem refreshbutton;
	private JSONCalendarTask downloadtask;
	
	//Initial function
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Inflate the layout into the parent view of container view of the parent class
		View rootView = inflater.inflate(R.layout.events_fragment, container, false);
		
		//Allow this fragment to interact with the menu
        setHasOptionsMenu(true);
        
        //initialize data
        events = new ArrayList<CalendarEvent>();
        
        //set an adapter up for the listview to handle displaying the data
        ListView callist = (ListView) rootView.findViewById(R.id.calendarlist);
        listadapter = new EventsListAdapter(this.getSherlockActivity(), events);
        callist.setAdapter(listadapter);
        
        //Start the download of the calendar data
        downloadtask = new JSONCalendarTask();
		downloadtask.execute();
        
       return rootView;
    }

	//Class to be run when the fragment is terminated
	@Override
	public void onStop(){
    	super.onStop();
    	//this class, for some reason, didn't like the logcat() function. Very strange.
    	if(PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).getBoolean("debugging", false)) Log.d("RPI", "Running onStop()");
    	//check the state of the Download() task
    	if(downloadtask != null && downloadtask.getStatus() == Status.RUNNING){
    		//if there is a download running stop it
    		if(PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).getBoolean("debugging", false)) Log.d("RPI", "Stopping Thread");	
    		downloadtask.cancel(true);
    		if(PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).getBoolean("debugging", false)) Log.d("RPI", "Thread Stopped");
    	}
    }
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		//Class called when the options menu is populated
		super.onCreateOptionsMenu(menu, inflater);
		//Add a refresh button and set its icon and visibility
		refreshbutton = menu.add("Refresh");
		refreshbutton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		refreshbutton.setIcon(R.drawable.navigation_refresh);
	}
	//Class called when an options item is selected
	public boolean onOptionsItemSelected(MenuItem item) {
		//logcat( "EventsFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	//refresh the data
        	downloadtask = new JSONCalendarTask();
    		downloadtask.execute();
        }
      //This passes the call back up the chain to the main class, which also handles onOptionsitemSeleced events
        return super.onOptionsItemSelected(item);
    }

	//AsyncTask thread to download calendar data
	private class JSONCalendarTask extends AsyncTask<Void, Void, Boolean> {
		
		private static final String events_JSON_URL = "http://events.rpi.edu/webcache/v1.0/jsonDays/31/list-json/no--filter/no--object.json";

		//before the thread is executed set the action bar to show indeterminate progress, usually a spinner
		protected void onPreExecute(){
			getSherlockActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
			events.clear();
		}
		
		//Class to be ran in another thread
		@Override
		protected Boolean doInBackground(Void... params) {
			//If a looper hasn't already been prepared by another thread prepare one for this application
			if (Looper.myLooper()==null) {
				 Looper.prepare();
			 }
			logcat( "Begining Download");
			String data;
			CalendarEvent temp = new CalendarEvent();
			//Try to download data
			try {
			data = ( (new HttpClient()).getData(events_JSON_URL));
			logcat( "downloaded data of length "+data.length());
			data = Util.unescapeHTML(data);
			}
			catch(Exception e){
				//if the download failed quit the thread and notify the user
				e.printStackTrace();
				Toast.makeText(getSherlockActivity(), "Events download failed. Try again later.", Toast.LENGTH_SHORT).show();
				return false;
			}
			//Try to read all of the JSON objects into their respective variables
			try {
				jObj = new JSONObject(data);
				logcat( "Parsing items");
				
				JSONArray items = jObj.getJSONObject("bwEventList").getJSONArray("events");
				JSONObject tempJ;
				
				//loop through each of the event items in the array
				for(int i = 0; i<items.length(); i++){
					logcat( "Adding item #"+i);
					temp = new CalendarEvent();
					
					tempJ = items.getJSONObject(i);
					
					logcat( "Getting variables");
					
					temp.setSummary(tempJ.getString("summary"));
					temp.setLink(tempJ.getString("eventlink"));
					temp.setAllDay(tempJ.getJSONObject("start").getBoolean("allday"));
					temp.setStartDate(tempJ.getJSONObject("start").getString("shortdate"));
					temp.setStartTime(tempJ.getJSONObject("start").getString("time"));
					temp.setEndDate(tempJ.getJSONObject("end").getString("shortdate"));
					temp.setEndTime(tempJ.getJSONObject("end").getString("time"));
					temp.setLocation(tempJ.getJSONObject("location").getString("address"));
					temp.setDescription(tempJ.getString("description"));
					
					events.add(temp);
					
					logcat( "Item saved: "+temp.getSummary());
				}
				

			} catch (JSONException e) {				
				e.printStackTrace();
				return false;
			}
			//Quit the looper now that we're done with it
			Looper.myLooper().quit();
			logcat( "Finished Download");
			return true;
	}

		@Override
		protected void onPostExecute(Boolean results)
		{
			//Set the action bar back to normal
			getSherlockActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
			if (!results)
			{
				Toast.makeText(getSherlockActivity(), "Events download failed. Try again later.", Toast.LENGTH_SHORT).show();
				return;
			}
		//code to be ran in the UI thread after the background thread has completed
			logcat( "Updating List");
			
			try{
				//Notify the list of new data
				listadapter.notifyDataSetChanged();
			}
			catch(Exception e){
				logcat( e.toString());
			}
		}

	private void logcat(String logtext){
		//code to write a log.d message if the user allows it in preferences
		if(PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).getBoolean("debugging", false))
			Log.d("RPI", logtext);
	}
  }
}