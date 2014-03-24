/**
 * Filename: ShuttlesFragment.java
 * 
 * The ShuttlesFragment class is an exact copy of the SherlockFragment class
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
 * Date: 3/24/2014
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
import static android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import static android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import static android.support.v4.app.Watson.OnPrepareOptionsMenuListener;

public class ShuttlesFragment extends SupportMapFragment implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, OnOptionsItemSelectedListener {
    private SherlockFragmentActivity mActivity;
    private GoogleMap map;
    private static final double UnionLat = 42.72997;
    private static final double UnionLon = -73.676649;
    private static final LatLng locationCoords = new LatLng(UnionLat, UnionLon);
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	/** Upon being selected by the user, this method is called to draw the map */
    {
    	super.onCreateView(inflater, container, savedInstanceState);
    	View rootView = inflater.inflate(R.layout.shuttles_fragment, container, false);
    	map = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.shuttlesview)).getMap();
    	map.setMyLocationEnabled(true);
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, 14));
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
    	ft.remove(fm.findFragmentById(R.id.shuttlesview));
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
