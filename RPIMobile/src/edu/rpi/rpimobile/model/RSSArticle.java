package edu.rpi.rpimobile.model;

import java.util.Date;

//Class for storing all of the variables associated with a RSS call
public final class RSSArticle
{
	private String title;
	private String link;
	private Date time;
	private String category;
	
	
	public RSSArticle deepcopy()
	{
		RSSArticle copy = new RSSArticle();
		copy.title = title;
		copy.link = link;
		copy.time = time;
		copy.category = category;
		
		return copy;
	}
	
	public String getTitle() { return new String(this.title); }
	public String getLink() { return new String(this.link); }
	public Date getTime() { return new Date(this.time.getTime()); }
	public String getCategory() { return new String(this.category); }
	public void setTitle(String title_) { this.title = title_; }
	public void setLink(String link_) { this.link = link_; }
	public void setTime(Date time_) { this.time = time_; }
	public void setCategory(String category_) { this.category = category_; }
	
	
}
