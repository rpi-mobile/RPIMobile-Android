package edu.rpi.rpimobile.model;

import java.util.Date;

//Class for storing all of the variables associated with a RSS call
public class RSSObject
{
	private String title;
	private String link;
	private Date time;
	private String category;
	
	
	public RSSObject deepcopy()
	{
		RSSObject copy = new RSSObject();
		copy.title = title;
		copy.link = link;
		copy.time = time;
		copy.category = category;
		
		return copy;
	}
	
	public String getTitle() { return this.title; }
	public String getLink() { return this.link; }
	public Date getTime() { return this.time; }
	public String getCategory() { return this.category; }
	public void setTitle(String title_) { this.title = title_; }
	public void setLink(String link_) { this.link = link_; }
	public void setTime(Date time_) { this.time = time_; }
	public void setCategory(String category_) { this.category = category_; }
	
	
}
