package edu.rpi.rpimobile.model;

//Class for storing all of the variables associated with a laundry call
public final class LaundryRoom implements Comparable<LaundryRoom>
{
	
	private String tag = "";
	private int available_washers = 0;
	private int available_dryers = 0;
	private int used_washers = 0;
	private int used_dryers = 0;
	private int laundryroom_url_number = 0;
	
	private static final String room_page_url = "http://www.laundryalert.com/cgi-bin/rpi2012/LMRoom?Halls=";
	
	// Getters:
	public String getTag() { return new String(this.tag); }	
	public int getAvailableWashers() { return Integer.valueOf(this.available_washers); }	
	public int getAvailableDryers()	{ return Integer.valueOf(this.available_dryers);	}	
	public int getUsedWashers()	{ return Integer.valueOf(this.used_washers);	}	
	public int getUsedDryers() { return Integer.valueOf(this.used_dryers); }
	public int getLaundryRoomURLNumber() { return Integer.valueOf(this.laundryroom_url_number); }
	public String getLaundryRoomPageURL() { return new String(room_page_url + laundryroom_url_number); }
	
	// Setters:
	public void setTag(String tag_)	{ this.tag = tag_; }
	public void setAvailableWashers(int available_) { this.available_washers = available_; }
	public void setAvailableDryers(int available_) { this.available_dryers = available_; }
	public void setUsedWashers(int used_) { this.used_washers = used_; }
	public void setUsedDryers(int used_) { this.used_dryers = used_; }
	public void setLaundryRoomURLNumber(int hall_num) { this.laundryroom_url_number = hall_num; }
	
	// Compare buildings according to their name
	@Override
	public int compareTo(LaundryRoom b) {
		return this.getTag().compareTo(b.getTag());
	}
}