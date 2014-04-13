/**
 * Filename: Shuttle.java
 * Author: Peter Piech
 * Date: 4/12/2014
 * Description: Shuttle is a simple class that
 * 				holds necessary data for each
 * 				marker to be placed in the
 * 				ShuttleFragment map.
 * Credits: This class is largely based
 * 			off of Gabe Perez's original
 * 			implementation of a class by
 * 			the same name.
 */

package edu.rpi.rpimobile.model;

public final class Shuttle
{
	private final int id;
	private final String name;
	private final int heading;
	private final double latitude;
	private final double longitude;
	private final int speed;
	private final String timestamp;
	private final String cardinal_point;
	private final String public_status_msg;
	
	public Shuttle(int _id, String _name, int _heading, double _latitude, double _longitude,
			int _speed, String _timestamp, String _cardinal_point, String _public_status_msg)
	{
		this.id = _id;
		this.name = _name;
		this.heading = _heading;
		this.latitude = _latitude;
		this.longitude = _longitude;
		this.speed = _speed;
		this.timestamp = _timestamp;
		this.cardinal_point = _cardinal_point;
		this.public_status_msg = _public_status_msg;
	}
	
	public int getId() { return Integer.valueOf(this.id); }
	public String getName() { return new String(this.name); }
	public int getHeading() { return Integer.valueOf(this.heading); }
	public double getLatitude() { return Double.valueOf(this.latitude); }
	public double getLongitude() { return Double.valueOf(this.longitude); }
	public int getSpeed() { return Integer.valueOf(this.speed); }
	public String getTimestamp() { return new String(this.timestamp); }
	public String getCardinalPoint() { return new String(this.cardinal_point); }
	public String getPublicStatusMsg() { return new String(this.public_status_msg); }

}
