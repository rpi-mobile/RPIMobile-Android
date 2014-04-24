package edu.rpi.rpimobile.model;


//Class for storing all of the variables associated with a Calendar call
public final class CalEvent {
	
	//public String title;
	private String summary;
	private String description;
	private String link;
	private String location;
	private boolean allday;
	private String starttime;
	private String endtime;
	private String startdate;
	private String enddate;
	
	public String getSummary() { return new String(this.summary); }
	public String getDescription() { return new String(this.description); }
	public String getLink() { return new String(this.link); }
	public String getLocation() { return new String(this.location); }	
	public boolean getAllDay() { return Boolean.valueOf(this.allday); }
	public String getStartTime() { return new String(this.starttime); }
	public String getEndTime() { return new String(this.endtime); } 
	public String getStartDate() { return new String(this.startdate); }
	public String getEndDate() { return new String(this.enddate); }
	
	public void setSummary(String summary_) { this.summary = summary_; }
	public void setDescription(String description_) { this.description = description_; }
	public void setLink(String link_) { this.link = link_; }
	public void setLocation(String location_) { this.location = location_; }	
	public void setAllDay(boolean allday_) { this.allday = allday_; }
	public void setStartTime(String starttime_) { this.starttime = starttime_; }
	public void setEndTime(String endtime_) { this.endtime = endtime_; } 
	public void setStartDate(String startdate_) { this.startdate = startdate_; }
	public void setEndDate(String enddate_) { this.enddate = enddate_; }
	
	
}
