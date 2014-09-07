package edu.rpi.rpimobile;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity
{
	
	//Very simple activity that launches a PreferenceActivity and populates it was a prefs.xml file
	@Override
	protected void onCreate(Bundle savedInstanceState)
	// TODO : update strings.xml arrays to allow for selection of newly added fragments
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);
	}
}
