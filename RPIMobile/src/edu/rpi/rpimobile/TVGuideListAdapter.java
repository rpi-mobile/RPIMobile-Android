/**
 * Filename: TVGuideListAdapter.java
 * Author: Peter Piech
 * Date: 3/15/2013
 * Description: TVGuideListAdapter class draws the ListView
 *              from the TVGuideFragment class.
 */

package edu.rpi.rpimobile;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.rpi.rpimobile.model.TVChannel;

public class TVGuideListAdapter extends BaseAdapter
{
	private Context context;
	private List<TVChannel> channels;
	private LayoutInflater inflater;
	
	public TVGuideListAdapter(Context context_, List<TVChannel> channels_)
	{
		this.context = context_;
		this.channels = channels_;
	}

	@Override
	public int getCount()
	{
		return channels.size();
	}

	@Override
	public Object getItem(int index)
	{
		return channels.get(index);
	}

	@Override
	public long getItemId(int index)
	{
		return index;
	}

	@Override
	public View getView(final int index, View convertView, ViewGroup parent)
	{
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.tvguide_list_item, parent, false);
		
		TextView tvNetworkName = (TextView) itemView.findViewById(R.id.networkName);
		TextView tvChannelNum = (TextView) itemView.findViewById(R.id.channelNumber);
		tvNetworkName.setText(channels.get(index).getNetworkName());
		tvChannelNum.setText(channels.get(index).getNumber());
		
		itemView.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(channels.get(index).getNetworkURL())));
				
			}
			
		});
		return itemView;
	}
}