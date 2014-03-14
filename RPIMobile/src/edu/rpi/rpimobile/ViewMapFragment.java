/**
 * Filename: ViewMapFragment.java
 * 
 * The ViewMapFragment class is an exact copy of the SherlockFragment class
 * the from ActionBarSherlock library except the name has been changed and
 * it extends SupportMapFragment instead of extending Fragment.
 * 
 * Additions to the class definition include:
 * 1. GoogleMap, String, and LatLng private variables
 * 2. Overridden method onCreateView
 * 3. Helper method setMapParameters
 * 
 * Additions/Edits by:
 * Author: Peter Piech
 * Date: 3/14/2013
 * 
 */

package edu.rpi.rpimobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import static android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import static android.support.v4.app.Watson.OnPrepareOptionsMenuListener;

public class ViewMapFragment extends SupportMapFragment implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, OnOptionsItemSelectedListener {
    private SherlockFragmentActivity mActivity;
    private GoogleMap map;
    private String locationName;
    private LatLng locationCoords;
    
    public void setMapParameters(String name, double lat, double lon)
    {
    	this.locationName = name;
    	locationCoords = new LatLng(lat, lon);
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/** Upon being selected by the user, this method is called to draw the map */
    {
    	super.onCreateView(inflater, container, savedInstanceState);
    	View rootView = inflater.inflate(R.layout.viewmap_fragment, container, false);
    	map = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();
    	map.setMyLocationEnabled(true);
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, 17));
    	Marker currMarker = map.addMarker(new MarkerOptions().title(locationName).position(locationCoords));
    	currMarker.showInfoWindow();
    	return rootView;
    }
    
    public SherlockFragmentActivity getSherlockActivity() {
        return mActivity;
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
    public void onDestroyView()
    {
    	super.onDestroyView();
    	FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.remove(fm.findFragmentById(R.id.mapview));
    	ft.commit();
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
}
