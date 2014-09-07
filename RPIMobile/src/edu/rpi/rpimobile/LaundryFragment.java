package edu.rpi.rpimobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import edu.rpi.rpimobile.model.LaundryRoom;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


//Laundry Fragment
public class LaundryFragment extends SherlockFragment {
    
	//All variables to be used throughout the function
    private List<LaundryRoom> laundryrooms;
    private ListView laundryroomlist;
    private LaundryListAdapter listadapter;
    private MenuItem refreshbutton;
    private AsyncTask<Void, Void, Boolean> downloadtask;
    
    private static String landing_page_url = "http://www.laundryalert.com/cgi-bin/rpi2012/LMPage";
    
    //Initial function
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	//Inflate the layout into the parent view of container view of the parent class
    	View rootView = inflater.inflate(R.layout.laundry_fragment, container, false);
    	
    	//Allow this fragment to interact with the menu
    	setHasOptionsMenu(true);
    	
    	//Point the laundryrooms variable to an ArrayList of LaundryRoom objects
        laundryrooms = new ArrayList<LaundryRoom>();
        
        //assign a list adapter to the listview to handle displaying the data
        laundryroomlist = (ListView) rootView.findViewById(R.id.laundrylist);
        listadapter = new LaundryListAdapter(this.getActivity(), laundryrooms);
        laundryroomlist.setAdapter(listadapter);
        
        //download the Laundry data
        downloadtask = new LaundryFragment.Download().execute();
        
        
        return rootView;
    }
    
  //Class to be run when the fragment is terminated
    @Override
	public void onStop(){
    	super.onStop();
    	logcat( "Running onStop()");
    	//check the state of the Download() task
    	if(downloadtask != null && downloadtask.getStatus() == Status.RUNNING){
    		//if there is a download running stop it
    		logcat( "Stopping Thread");	
    		downloadtask.cancel(true);
    		logcat( "Thread Stopped");
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
	
	public boolean onOptionsItemSelected(MenuItem item) {
		//Class called when an options item is selected
		logcat( "LaundryFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	
        	//Download the weather again
        	if(downloadtask != null && downloadtask.getStatus() != Status.RUNNING)
        	{
        		downloadtask = new LaundryFragment.Download().execute();
        	}
        	
        }
        
      //This passes the call back up the chain to the main class, which also handles onOptionsitemSeleced events
        return super.onOptionsItemSelected(item);
    }
	
	//AsynchTask thread to download laundry data
    private class Download extends AsyncTask<Void, Void, Boolean>{
    		
    	//before the thread is executed set the action bar to show indeterminate progress, usually a spinner
    	protected void onPreExecute(){
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
			laundryrooms.clear(); // empty the laundryrooms to avoid duplicates
		}
    	
    		
    	//Class to be ran in another thread
    		@Override
    		protected Boolean doInBackground(Void... params) {
    			
    			//temp variable for storing each laundryroom
    			LaundryRoom temp = new LaundryRoom();
    			//temp variable for the website source
    			String source = "";
    			
    			logcat( "Beginning download");
    			
    			try {
    				//try to download the source of the webpage
    				HttpClient httpClient = new DefaultHttpClient();
        			HttpGet get = new HttpGet(landing_page_url);
        			
					HttpResponse response = httpClient.execute(get);
					
					source = EntityUtils.toString(response.getEntity());
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			
    			//This code parses the webpage source and saves each laundry room's name, free washers and dryers, and used washers and dryers.
    			//It is just a simple scrape of the webpage that will be phased out as soon as LaundryAlert has a public API, or RPIMobile 
    			//has it's own server for data like this.
    			
    			logcat( "Source download Length: "+source.length());
    			//logcat( source);
    			String[] results = source.split("\\s+");
    			int counter = 0;
    			int j = 0; // used to enumerate the objects for URL setting
    			for(int i = 0; i<results.length; ++i){
    				if(results[i].contains("sans-serif")){
    					++counter;
    					if(counter > 8 && !(results[i+1].equals("On") && results[i+2].equals("site"))){
	    					temp = new LaundryRoom();
	    					
	    					temp.setTag(results[i].substring(12));
	    					
	    					logcat( temp.getTag());
	    					
	    					++i;
	    					while(!results[i].contains("font")){
	    						logcat( "Concatinating: "+results[i]);
	    						temp.setTag(temp.getTag() +" "+results[i]);
	    						++i;
	    					}
	    					logcat( temp.getTag());
	    					
	    					while(!results[i].contains("sans-serif")) ++i;
	    					++i;
	    					temp.setAvailableWashers(Integer.parseInt(results[i]));
	    					logcat("" + temp.getAvailableWashers());
	    					
	    					while(!results[i].contains("sans-serif")) ++i;
	    					++i;
	    					temp.setAvailableDryers(Integer.parseInt(results[i]));
	    					
	    					while(!results[i].contains("sans-serif")) ++i;
	    					++i;
	    					temp.setUsedWashers(Integer.parseInt(results[i]));
	    					
	    					while(!results[i].contains("sans-serif")) ++i;
	    					++i;
	    					temp.setUsedDryers(Integer.parseInt(results[i]));
	    					
	    					temp.setLaundryRoomURLNumber(j);
	    					laundryrooms.add(temp);
	    					++j;
	    				}
    				}
    			}
    			
    	        logcat( "Exiting AsynchTask");
    			return true;
    		}
    		
    		protected void onPostExecute(Boolean results) {
    			//code to be ran in the UI thread after the background thread has completed
    			logcat( "Notifying list");
    			// sort the laundryrooms ArrayList so that it displays in alphabetical order
    			Collections.sort(laundryrooms);
    			//Set the action bar back to normal
    			getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
    			try{ 
    				//if the fragment is visible update the list adapter
    				if(LaundryFragment.this.isVisible())
    					listadapter.notifyDataSetChanged();
    				else {
    					logcat( "Canceling view, Fragment 2 not visible");
    				}
    			}
    			catch(Exception e){
    				logcat( e.toString());
    			}
    		}    		
    	}


	private void logcat(String logtext){
		//code to write a log.d message if the user allows it in preferences
		if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("debugging", false))
			Log.d("RPI", logtext);
	}
}

