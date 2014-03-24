package edu.rpi.rpimobile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherHttpClient
{	
	
	private final static String IMG_URL = "http://openweathermap.org/img/w/";
	private final static String RequestMethod = "GET";
	private final static String endLine = "\r\n";

	//function to pull a string of data from an http address
	public String getWeatherData(String location) {
		//initial variables
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			//create a connection, set its parameters, and open it
			con = (HttpURLConnection) ( new URL(location)).openConnection();
			con.setRequestMethod(RequestMethod);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();

			//Read the response
			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + endLine);
			//close the Stringbuffer and http connection
			is.close();
			con.disconnect();
			return buffer.toString();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;

	}
	//Code to download the OpenWeatherMap icon
	public byte[] getImage(String code)
	// 3/23/2014: Method rewrite by Peter Piech
	{
		try {
			URL url = new URL(IMG_URL + code);
			InputStream is = (InputStream) url.getContent();
			byte[] buffer = new byte[1024];
			int bytesRead;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((bytesRead = is.read(buffer)) != -1)
			{
				baos.write(buffer, 0, bytesRead);
			}
			return baos.toByteArray();
	    }
		catch(MalformedURLException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}
}