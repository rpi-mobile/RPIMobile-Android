/**
 * Filename: ShuttlesFragment.java
 * 
 * The ShuttlesFragment class is an exact copy of the SherlockFragment class
 * the from ActionBarSherlock library except the name has been changed and
 * it extends SupportMapFragment instead of extending Fragment.
 * 
 * Additions to the class definition include:
 * 1. Two custom helper AsyncTask<Void, Void, Boolean> classes
 *    for download the KML and JSON data from shuttles.rpi.edu
 * 2. GoogleMap, String, and LatLng private variables among many
 *    other custom data structures
 * 3. Overridden method onCreateView
 * 
 * Additions/Edits by:
 * Author: Peter Piech
 * Date: 4/12/2014
 * 
 * Credits:
 * ShuttlesPositionsDownload's doInBackground() method
 * is heavily based upon Gabe Perez's initial implementation
 * of a class with a different name (ShuttleFragment).
 * 
 */

package edu.rpi.rpimobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.rpi.rpimobile.model.Shuttle;
import edu.rpi.rpimobile.model.ShuttlesRoutes;
import static android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import static android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import static android.support.v4.app.Watson.OnPrepareOptionsMenuListener;

public class ShuttlesFragment extends SupportMapFragment implements OnCreateOptionsMenuListener,
																	OnPrepareOptionsMenuListener,
																	OnOptionsItemSelectedListener
{
    private SherlockFragmentActivity mActivity;
    private GoogleMap map;
    private ShuttlesRoutes routeOverlay;
    private ShuttlesRoutesDownload routesDownloadTask;
    private ShuttlesPositionsDownload positionsDownloadTask;
    private ArrayList<Shuttle> shuttles;
    private ArrayList<Marker> shuttleMarkers;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/** Upon being selected by the user, this method is called to draw the map */
    {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	final double UnionLat = 42.72997;
    	final double UnionLon = -73.676649;
    	final LatLng centerCoords = new LatLng(UnionLat, UnionLon);
    	
    	routeOverlay = new ShuttlesRoutes();
    	shuttles = new ArrayList<Shuttle>();
    	shuttleMarkers = new ArrayList<Marker>();
    	
    	View rootView = inflater.inflate(R.layout.shuttles_fragment, container, false);
    	map = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.shuttlesview)).getMap();
    	map.setMyLocationEnabled(true);
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerCoords, 14));
    	
    	routesDownloadTask = new ShuttlesRoutesDownload();
    	routesDownloadTask.execute();
    	
    	updateShuttlePositionsAsynchronously();
    	
    	return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof SherlockFragmentActivity)) {
            throw new IllegalStateException(getClass().getSimpleName() + " must be attached to a SherlockFragmentActivity.");
        }
        mActivity = (SherlockFragmentActivity)activity;

        super.onAttach(activity);
    }
    
    @Override
	public void onStop()
    {
    	super.onStop();
    	if (routesDownloadTask != null && routesDownloadTask.getStatus() == Status.RUNNING)
    	{
    		routesDownloadTask.cancel(true);
    	}
    	if (positionsDownloadTask != null && positionsDownloadTask.getStatus() == Status.RUNNING)
    	{
    		positionsDownloadTask.cancel(true);
    	}
    }
    
    @Override
    public void onDestroyView()
    {
    	super.onDestroyView();
    	FragmentManager fm = mActivity.getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.remove(fm.findFragmentById(R.id.shuttlesview));
    	try
    	// avoid RuntimeException: IllegalStateException
    	// caused by calling commit() after saveInstanceState
    	// has been called on the activity already.
    	// (i.e. the user hits the back button to kill
    	//  the app)
    	{
    		ft.commit();
    	}
    	catch (Exception e)
    	{ } // we don't need to do anything. the app is killed anyway!
    	
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater) {
        onCreateOptionsMenu(new MenuWrapper(menu), mActivity.getSupportMenuInflater());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Nothing to see here.
    }

    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        onPrepareOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Nothing to see here.
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return onOptionsItemSelected(new MenuItemWrapper(item));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Nothing to see here.
        return false;
    }
    
    private void updateShuttlePositionsAsynchronously()
    {
    	final Handler handler = new Handler();
    	Timer timer = new Timer();
    	TimerTask performUpdate = new TimerTask()
    	{
    		@Override
    		public void run()
    		{
    			handler.post(new Runnable()
    			{
    				@Override
					public void run()
    				{
    					try
    					{
    						positionsDownloadTask = new ShuttlesPositionsDownload();
    						positionsDownloadTask.execute();
    					}
    					catch (Exception e) { }
    				}
    				
    			});
    		}
    	};
    	timer.schedule(performUpdate, 0, 14750);
    }
    
    private class ShuttlesRoutesDownload extends AsyncTask<Void, Void, Boolean>
    /**
     * Member class to populate the data structures that contain
     * the data for the routes to be used on the GoogleMap.
     */
    {
    	private static final String netlink_KML_URL = "http://shuttles.rpi.edu/displays/netlink.kml";
    	private static final String netlink_KML_pattern = "//Placemark/name/text()|//coordinates/text()";
    	
    	private NodeList retrieveNodeList(String source_file_url, String xpath_pattern_expression)
    			throws ParserConfigurationException, IOException, SAXException, XPathExpressionException
    	{
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
	        factory.setIgnoringElementContentWhitespace(true);
	        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        			
	        URL KML_URL = new URL(source_file_url);
			InputStream is = KML_URL.openStream();
	        
			Document document = builder.parse(new InputSource(is));
			
			document.getDocumentElement().normalize();
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        
	        NodeList result = (NodeList) xpath.evaluate(xpath_pattern_expression, document, XPathConstants.NODESET);
    		
    		return result;
    	}
    	
    	@Override
    	protected void onPreExecute()
    	{
			mActivity.setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}
    	
		@Override
		protected Boolean doInBackground(Void... params)
		{
			/* ============================================================
	         * NETLINK.KML
	         * ============================================================
	         */
			
			NodeList KML_result = null;
			try // download and parse the netlink.kml file
			{
				KML_result = this.retrieveNodeList(netlink_KML_URL, netlink_KML_pattern);
			}
			catch (Exception e)
			{
				return false;
			}
			
			/*
	         * =======================================
	         * CAMPUS SHUTTLE ROUTES POLYLINE PARSING:
	         * =======================================
	         * netlink.kml:
	         * West Campus is first -> Route Color = RED
	         * East Campus is next  -> Route Color = GREEN
	         * 
	         * NodeList structure:
	         * String item(0).getNodeValue() == "West Campus" 
	         * String item(1).getNodeValue() == West Campus route coordinates
	         * String item(2).getNodeValue() == "East Campus"
	         * String item(3).getNodeValue() == East Campus route coordinates
	         * 
	         * */
			if (KML_result == null)
			{
				return false;
			}
			else if (KML_result.getLength() >= 4)
	        {
	        	for (int i = 0; i < 2; ++i)
	        	{
	        		PolylineOptions currPath = new PolylineOptions();
	        		currPath.width(5);
	        		if (i == 0) // West Campus Route
	        		{
	        			currPath.color(Color.RED);
	        		}
	        		else if (i == 1) // East Campus Route
	        		{
	        			currPath.color(Color.GREEN);
	        		}
	        		
	        		int coordIndex = 2 * i + 1; // 2*i is name, 2*i + 1 is coordinates
	        		
	        		Node n = KML_result.item(coordIndex); // Fetch node with name first
	        		String[] points = n.getNodeValue().trim().split("\\s+");
	        		for (String point: points)
	        		{
	        			Scanner in = new Scanner(point);
	        			in.useDelimiter(",");
	        			double longitude;
	        			double latitude;
	        			if (in.hasNextDouble())
	        			{
	        				longitude = in.nextDouble();
	        			}
	        			else
	        			{
	        				in.close();
	        				return false;
	        			}
	        			if (in.hasNextDouble())
	        			{
	        				latitude = in.nextDouble();
	        			}
	        			else
	        			{
	        				in.close();
	        				return false;
	        			}
	        			LatLng nextPoint = new LatLng(latitude, longitude);
	        			currPath.add(nextPoint);	        			
	        			in.close();
	        		}
	        		
	        		routeOverlay.add(currPath);
	        	}
	        }
			
			
			/*
	         * =============================
	         * CAMPUS SHUTTLE STOPS PARSING:
	         * =============================
	         * netlink.kml:
	         * 
	         * NodeList structure:
	         * String item(4).getNodeValue() == "Student Union" 
	         * String item(5).getNodeValue() == Student Union coordinates
	         * ... etc. etc.
	         * 
	         * */
	        if (KML_result.getLength() > 4)
	        {
	        	for (int i = 4; i < KML_result.getLength() - 1; ++i)
	        	{
	        		MarkerOptions currStop = new MarkerOptions();
	     	        currStop.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot));
	     	        
	     	        String stopName = KML_result.item(i).getNodeValue();
	     	        
	     	        ++i; // now the ith element is the coordinates for the stop
	     	        String point = KML_result.item(i).getNodeValue();
	     	        
	     	        Scanner in = new Scanner(point);
	     	        in.useDelimiter(",");
	     	        double longitude;
	     	        double latitude;
	     	        if (in.hasNextDouble())
	     	        {
	     	        	longitude = in.nextDouble();
	     	        }
	     	        else
	     	        {
	     	        	in.close();
	     	        	return false;
	     	        }
	     	        if (in.hasNextDouble())
	     	        {
	     	        	latitude = in.nextDouble();
	     	        }
	     	        else
	     	        {
	     	        	in.close();
	     	        	return false;
	     	        }
	     	        
	     	        LatLng nextPoint = new LatLng(latitude, longitude);
	     	        currStop.title(stopName);
	     	        currStop.position(nextPoint);
	     	        
	     	        routeOverlay.add(currStop);
	     	        
	     	        in.close();
	        	}
	        }
	        else
	        {
	        	return false;
	        }
	        return true;
		}
		
		@Override
		protected void onPostExecute(Boolean success)
		{
			if (success)
			{
				routeOverlay.publishTo(map);
			}
			else
			{
				Toast.makeText(mActivity, "Routes download failed. Restart the app later.", Toast.LENGTH_LONG).show();
			}
			mActivity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
    	
    }
    
    private class ShuttlesPositionsDownload extends AsyncTask<Void, Void, Boolean>
    /**
     * Member class to populate the data structures that contain
     * the data for the positions to be use in the GoogleMap
     */
    {
    	private static final String current_JS_URL = "http://shuttles.rpi.edu/vehicles/current.js";
    	
    	
    	@Override
    	protected void onPreExecute()
    	{
    		mActivity.setProgressBarIndeterminateVisibility(Boolean.TRUE);
    	}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(current_JS_URL);
				HttpResponse response = httpClient.execute(get);
				
				String raw_data = EntityUtils.toString(response.getEntity());
				JSONArray shuttleJSON = new JSONArray(raw_data);
				
				for (int i = 0; i < shuttleJSON.length(); ++i)
				{
					JSONObject obj = shuttleJSON.getJSONObject(i);
					
					JSONObject aVehicle = obj.getJSONObject("vehicle");
					JSONObject latestPosition = aVehicle.getJSONObject("latest_position");
					
					int temp_id = aVehicle.getInt("id");
					String temp_name = aVehicle.getString("name");
					
					int temp_heading = latestPosition.getInt("heading");
					double temp_latitude = Double.parseDouble(latestPosition.getString("latitude"));
					double temp_longitude = Double.parseDouble(latestPosition.getString("longitude"));
					int temp_speed = latestPosition.getInt("speed");
					String temp_timestamp = latestPosition.getString("timestamp");
					String temp_cardinal_point = latestPosition.getString("cardinal_point");
					String temp_public_status_msg = latestPosition.getString("public_status_msg");
					
					shuttles.add(new Shuttle(temp_id, temp_name, temp_heading,
							temp_latitude,temp_longitude, temp_speed, temp_timestamp,
							temp_cardinal_point, temp_public_status_msg));
				}
				
			}
			catch (Exception e)
			{
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean success)
		{
			if (success)
			{
				for (Marker marker : shuttleMarkers)
				{
					marker.remove();
				}
				shuttleMarkers.clear();
				for (Shuttle shuttle : shuttles)
				{
					LatLng position = new LatLng(shuttle.getLatitude(), shuttle.getLongitude());
					String description = "Traveling " + shuttle.getCardinalPoint() + " at " + shuttle.getSpeed() + "mph";
					shuttleMarkers.add(map.addMarker(new MarkerOptions().position(position)
							.title(shuttle.getName()).snippet(description).flat(true)
							.anchor(0.5F, 0.5F).rotation(shuttle.getHeading())
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_icon))));
				}
				shuttles.clear();
			}
			else
			{
				Toast.makeText(mActivity, "Shuttle positions download failed. Retry again later.", Toast.LENGTH_LONG).show();
			}
			mActivity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
    	
    }
}