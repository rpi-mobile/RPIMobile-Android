/**
 * Filename: MapLocation.java
 * Author: Peter Piech
 * Date: 12/1/2013
 * Description: MapLocation is a simple class that
 *              holds necessary data for each element
 *              in the ListView created by MapFragment.
 */

package edu.rpi.rpimobile.model;

public final class MapLocation {
	
	private final String location_name;
	private final double latitude;
	private final double longitude;
	
	public MapLocation(String location_name_, double latitude_, double longitude_)
	{
		this.location_name = location_name_;
		this.latitude = latitude_;
		this.longitude = longitude_;
	}
	
	public String getName() { return new String(this.location_name); }
	public double getLatitude() { return Double.valueOf(this.latitude); }
	public double getLongitude() { return Double.valueOf(this.longitude); }
	
	@Override
	public String toString()
	{
		return new String(this.location_name);
	}

}
