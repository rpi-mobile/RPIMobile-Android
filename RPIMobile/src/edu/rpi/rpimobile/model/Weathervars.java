package edu.rpi.rpimobile.model;

//Class for storing all of the variables associated with a weather call
public class Weathervars{
	private String location;
	private Float temperature;
	private Float temphigh;
	private Float templow;
	private Float precipchance;
	private String condition;
	private byte[] icon;
	
	//create method to initialize all variables
	public Weathervars()
	{
		location = "";
		temperature = (float)0;
		temphigh = (float)0;
		templow = (float)0;
		precipchance = (float)0;
		condition = "";
		icon = null;
	}
	public String getLocation() { return this.location; }
	public Float getTemperature() { return this.temperature; }
	public Float getTempHigh() { return this.temphigh; }
	public Float getTempLow() { return this.templow; }
	public Float getChanceOfPrecipitation() { return this.precipchance; }
	public String getCondition() { return this.condition; }
	public byte[] getIcon() { return this.icon; }
	
	public void setLocation(String location_) { this.location = location_; }
	public void setTemperature(Float temperature_) { this.temperature = temperature_; }
	public void setTempHigh(Float temphigh_) { this.temphigh = temphigh_; }
	public void setTempLow(Float templow_) { this.templow = templow_; }
	public void setChanceOfPrecipitation(Float precipchance_) { this.precipchance = precipchance_; }
	public void setCondition(String condition_) { this.condition = condition_; }
	public void setIcon(byte[] icon_) { this.icon = icon_; }
}