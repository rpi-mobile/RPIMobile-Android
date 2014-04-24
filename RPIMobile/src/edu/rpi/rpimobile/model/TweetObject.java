package edu.rpi.rpimobile.model;

import java.util.Date;

//Class for storing all of the variables associated with a twitter call
public final class TweetObject{
	
	private String avatar;
	private String username;
	private Date time;
	private String body;
	
	//Deep copy method to return a new object with no links to the original
	public TweetObject deepcopy()
	{
		TweetObject temp = new TweetObject();
		temp.avatar = avatar;
		temp.username = username;
		temp.time = time;
		temp.body = body;
		return temp;
	}
	
	public String getAvatar() { return new String(this.avatar); }
	public String getUsername() { return new String(this.username); }
	public Date getTime() { return new Date(this.time.getTime()); }
	public String getBody() { return new String(this.body); }
	public void setAvatar(String avatar_) { this.avatar = avatar_; }
	public void setUsername(String username_) { this.username = username_; }
	public void setTime(Date time_) { this.time = time_; }
	public void setBody(String body_) { this.body = body_; }
	
}
