package edu.rpi.rpimobile.model;

//Class for storing all of the variables associated with a laundry call
public final class Building
{
	
	private String tag = "";
	private int available_washers = 0;
	private int available_dryers = 0;
	private int used_washers = 0;
	private int used_dryers = 0;
	
	public String getTag() { return this.tag; }	
	public int getAvailableWashers() { return this.available_washers; }	
	public int getAvailableDryers()	{ return this.available_dryers;	}	
	public int getUsedWashers()	{ return this.used_washers;	}	
	public int getUsedDryers() { return this.used_dryers; }	
	public void setTag(String tag_)	{ this.tag = tag_; }
	public void setAvailableWashers(int available_) { this.available_washers = available_; }
	public void setAvailableDryers(int available_) { this.available_dryers = available_; }
	public void setUsedWashers(int used_) { this.used_washers = used_; }
	public void setUsedDryers(int used_) { this.used_dryers = used_; }
	
}