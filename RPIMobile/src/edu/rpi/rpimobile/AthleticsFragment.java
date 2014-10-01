package edu.rpi.rpimobile;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import edu.rpi.rpimobile.model.RSSArticle;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
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

//Sports news feeds
public class AthleticsFragment extends SherlockFragment
{
	//All variables to be used throughout the function
	private ArrayList<RSSArticle> stories;
	private ArrayList<RSSArticle> tempstories;
	private ArrayList<RSSArticle> finlist = new ArrayList<RSSArticle>();
	private RSSArticle temp;
	private ListView rsslist;
	private RSSListAdapter rssadapter;
	private MenuItem refreshbutton;
	private AsyncTask<String, Void, Boolean> downloadtask;
	
	//Initial function
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Inflate the layout into the parent view of container view of the parent class
        View rootView = inflater.inflate(R.layout.rsslayout, container, false);
      
        //Allow this fragment to interact with the menu
        setHasOptionsMenu(true);
        
        //initialize data
        stories = new ArrayList<RSSArticle>();
        tempstories = new ArrayList<RSSArticle>();
        
        
        //set an adapter up for the listview to handle displaying the data
        rsslist = (ListView) rootView.findViewById(R.id.rsslist);
        rssadapter = new RSSListAdapter(this.getActivity(), this, stories);
        rsslist.setAdapter(rssadapter);
        
        //Initialize the download cycle to 0
        cyclenum = 0;
        //start the download cycle
        refreshcycle();

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
	
  //Class called when an options item is selected
	public boolean onOptionsItemSelected(MenuItem item) {
		logcat( "AthleticsFragment: onOptionsItemSelected");
		//If the refresh button was pressed
        if (item == refreshbutton){
        	//refresh the data
        	if(downloadtask != null && downloadtask.getStatus() != Status.RUNNING)
        	{
        		refreshcycle();
        	}
        	
        }
      //This passes the call back up the chain to the main class, which also handles onOptionsitemSeleced events
        return super.onOptionsItemSelected(item);
    }
	
	//Function to be called when the user scrolls to the end of a page
	public void loadpage(int pagenum){
		//Toast.makeText(getActivity(), "Loading page", Toast.LENGTH_SHORT).show();
	}
	
	//A variable to keep position in the refreshcycle()
	public int cyclenum;
	
	//The tags and rss links to be used in the refreshcycle. 
	//These can be expanded upon or edited at will as long as the pairing is maintained
	public final String[] tags = {"Top","Baseball","Field Hockey","Football","General","M Basketball","M Cross Country",
			"M Golf","M Hockey","M Indoor Track","M Lacrosse","M Soccer","M Swimming Diving","M Tennis","M Track Field","Softball",
			"W Cross Country","W Ice Hockey","W Indoor Track","W Lacrosse","W Soccer","W Swimming Diving","W Tennis","W Track Field",
			"W Basketball"};
	public final String[] rsslinks = {"http://www.rpiathletics.com/rss.aspx", "http://www.rpiathletics.com/rss.aspx?path=baseball"
			,"http://www.rpiathletics.com/rss.aspx?path=fhockey","http://www.rpiathletics.com/rss.aspx?path=football"
			,"http://www.rpiathletics.com/rss.aspx?path=gen","http://www.rpiathletics.com/rss.aspx?path=mbball"
			,"http://www.rpiathletics.com/rss.aspx?path=mcross","http://www.rpiathletics.com/rss.aspx?path=golf"
			,"http://www.rpiathletics.com/rss.aspx?path=hockey","http://www.rpiathletics.com/rss.aspx?path=indoortrack"
			,"http://www.rpiathletics.com/rss.aspx?path=mlax","http://www.rpiathletics.com/rss.aspx?path=msoc"
			,"http://www.rpiathletics.com/rss.aspx?path=mswim","http://www.rpiathletics.com/rss.aspx?path=mten"
			,"http://www.rpiathletics.com/rss.aspx?path=mtrack","http://www.rpiathletics.com/rss.aspx?path=softball"
			,"http://www.rpiathletics.com/rss.aspx?path=wcross","http://www.rpiathletics.com/rss.aspx?path=whock"
			,"http://www.rpiathletics.com/rss.aspx?path=windoortrack","http://www.rpiathletics.com/rss.aspx?path=wlax"
			,"http://www.rpiathletics.com/rss.aspx?path=wsoc","http://www.rpiathletics.com/rss.aspx?path=wswim"
			,"http://www.rpiathletics.com/rss.aspx?path=wten","http://www.rpiathletics.com/rss.aspx?path=wtrack"
			,"http://www.rpiathletics.com/rss.aspx?path=wbball"};

	//The new and improved version of the refreshcycle.
	//This allows the various rss feeds to be downloaded sequentially
	public void refreshcycle(){
		
		//shared preference object. To retrieve the user preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		if(cyclenum == 0){
			//for the first item set the action bar to show indeterminate progress and clear the list of stories
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
			stories.clear();
		}
		
		
		if(cyclenum == tags.length){
			//if it's the last item set the action bar back to normal and reset the cycle counter
			getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
    		logcat( "Loading complete List size:"+stories.size());
			cyclenum = 0;
		}
		else{
			//for all other items save the cycle number
			int ctemp = cyclenum;
			//increment the cycle number
			++cyclenum;
			//the first object is defaulted to true and all others are false by defualt
			//so the default bit of the shared preference call is ctemp==0, 
			//which is true for the 0th item and false for all other items
			
			if(prefs.getBoolean("sports_"+tags[ctemp], ctemp==0)){
				//if the user wants this feed then download it
				downloadtask = new AthleticsFragment.Download().execute(rsslinks[ctemp],tags[ctemp]);
				
			}
			else{
				//if the user doesn't want this feed then continue the cycle
				refreshcycle();
			}
		}
	
	}
	
	//Async Thread to download the RSS data in the background
    private class Download extends AsyncTask<String, Void, Boolean>{

    	//placeholder for future use
    	protected void onPreExecute(){
			
		}
    	
    	//Class to be ran in another thread
		@Override
		protected Boolean doInBackground(String... params) {
			//clear the temporary list
			tempstories.clear();
			logcat( "Begin doInBackground");
			RSSReader reader = new RSSReader();
			try {
				//try to download all of the RSS data
				logcat( "Initializing variables");
				//This project uses the android-rss library to handle all RSS calls: https://github.com/ahorn/android-rss
				String uri = params[0];
				logcat( "Downloading feed items");
				RSSFeed feed = reader.load(uri);
				logcat( "Assigning list");
				List<RSSItem> feedlist = feed.getItems();
				logcat( "Parsing feed");
				for(int i = 0; i<feedlist.size(); i++){
					//for each item populate the temporary RSSObject with all data
					temp = new RSSArticle();
					temp.setTitle(feedlist.get(i).getTitle());
					temp.setLink(feedlist.get(i).getLink().toString());
					temp.setTime(feedlist.get(i).getPubDate());
					temp.setCategory(params[1]);
					//add the temporary object to the temporary list
					tempstories.add(temp);
				}
				logcat( "Feed parsed");

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			finally
			{
				reader.close();
			}
	        
	        logcat( "Exiting AsynchTask");
			return true;
		}
		
		protected void onPostExecute(Boolean results)
		{
			//Set the action bar back to normal
			getSherlockActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
			if (!results)
			{
				Toast.makeText(getSherlockActivity(), "Athletics download failed. Try again later.", Toast.LENGTH_LONG).show();
				return;
			}
			//code to be ran in the UI thread after the background thread has completed
			logcat( "Notifying list");
			
			//combine the temp list with the "stories" list
			addtolist(tempstories);
			
			try{ 
				//Notify the listadapter to update the data
				rssadapter.notifyDataSetChanged();
			}
			catch(Exception e){
				logcat( e.toString());
			}
			//continue the refresh cycle
			refreshcycle();
		}
	}
    
    //class to add objects to the main list
    private void addtolist(ArrayList<RSSArticle> temp){
    	
    	logcat("Combining lists");
    	logcat("Source list: "+stories.size()+" Temp list: "+temp.size());
    	
    	//start each counter at 0
    	int sourcecounter = 0;
    	int tempcounter = 0;

    	//temporary list
    	finlist.clear();
    	
    	//loop through each list, adding them, most recent first, to the temp list
    	while(sourcecounter<stories.size() & tempcounter <temp.size()){
    		logcat("Looping, Source:"+sourcecounter+" Temp:"+tempcounter);
    		if(stories.get(sourcecounter).getTime().after(temp.get(tempcounter).getTime())){
    			finlist.add(stories.get(sourcecounter).deepcopy());
    			sourcecounter++;
    		}
    		else if(stories.get(sourcecounter).getTime().before(temp.get(tempcounter).getTime())){
    			finlist.add(temp.get(tempcounter).deepcopy());
    			tempcounter++;
    		}
    		else{
    			logcat( "Two items actually had the same time... Strange");
    			finlist.add(stories.get(sourcecounter).deepcopy());
    			sourcecounter++;
    		}
    	}
    	//add the remaining objects to the list
		while(sourcecounter<stories.size()){
			logcat("Clearing source");
			finlist.add(stories.get(sourcecounter).deepcopy());
			sourcecounter++;
		}
		while(tempcounter<temp.size()){
			logcat("clearing temp");
			finlist.add(temp.get(tempcounter).deepcopy());
			tempcounter++;
		}
		
		// If "Top Stories" is selected and any other feed is selected, then
		// if that feed contains a story that is also in "Top Stories", then
		// it will appear twice in the ArrayList, so removing duplicates is
		// necessary:		
		for (int i = 0; i < finlist.size(); ++i)
		{
			for (int j = i; j < finlist.size(); ++j)
			{
				if (i == j)
				{
					continue;
				}
				else if (finlist.get(i).getTitle().equals(finlist.get(j).getTitle()))
				{
					finlist.remove(j); // i is usually the category "Top" and we prefer to remove the other
				}
			}
		}
		
		//Assign the temporary list to the "stories" list
    	assign(stories, finlist);  	
    	logcat( "Lists combined. Source list:"+stories.size());
    }

    //Class to deal with the problem of copying ArrayLists in Java
    //Because ArrayLists really just store pointers to their objects a deepcopy must be made of each
    //item and passed to the list individually. This is much more efficient than using the 
    //java.serialize class to do this automatically.
    private void assign(ArrayList<RSSArticle> target, ArrayList<RSSArticle> source){
    	logcat( "Starting copy source:"+source.size()+" Target:"+target.size());
    	target.clear();
    	//clear the target list
    	for(int i = 0; i<source.size(); i++)
    		//add a deepcopy of each item to the target list
    		target.add(source.get(i).deepcopy());
    	
    	logcat( "Finished copy source:"+source.size()+" Target:"+target.size());
    }
    
    private void logcat(String logtext){
		//code to write a log.d message if the user allows it in preferences
		if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("debugging", false))
			Log.d("RPI", logtext);
	}
}