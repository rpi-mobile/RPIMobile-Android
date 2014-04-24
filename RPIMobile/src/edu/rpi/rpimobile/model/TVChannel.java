/**
 * Filename: TVChannel.java
 * Author: Peter Piech
 * Date: 3/15/2014
 * Description: TVChannel is a simple class that
 *              holds necessary data for each element
 *              in the ListView created by TVGuideFragment.
 */

package edu.rpi.rpimobile.model;

public final class TVChannel
{
	private final String network_name;
	private final String network_number;
	private final String network_url;
	
	public TVChannel(String network, String number, String URL)
	{
		this.network_name = network;
		this.network_number = number;
		this.network_url = URL;
	}
	
	public String getNetworkName() { return new String(this.network_name); }
	public String getNumber() { return new String(this.network_number); }
	public String getNetworkURL() { return new String(this.network_url); }

}
