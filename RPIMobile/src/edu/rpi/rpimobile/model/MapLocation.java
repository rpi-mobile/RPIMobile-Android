/**
 * Filename: MapLocation.java
 * Author: Peter Piech
 * Date: 12/1/2013
 * Description: MapLocation is a simple class that
 *              holds necessary data for each element
 *              in the ListView created by MapFragment.
 */

package edu.rpi.rpimobile.model;

public class MapLocation {
	
	private String location_name;
	private double latitude;
	private double longitude;
	
	public MapLocation(String location_name_, double latitude_, double longitude_)
	{
		this.location_name = location_name_;
		this.latitude = latitude_;
		this.longitude = longitude_;
	}
	
	public String getName() { return this.location_name; }
	public double getLatitude() { return this.latitude; }
	public double getLongitude() { return this.longitude; }

}
