package it.feio.android.checklistview.demo;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements PreferenceChangeListener{
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent pce) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putBoolean("refresh", true).commit();
		
	}
	
}
