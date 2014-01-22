package it.feio.android.checklistview.demo;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	Button b;
	View switchView;
	private Activity mActivity;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mActivity = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		switchView = findViewById(R.id.edittext);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (switchView!= null && prefs.getBoolean("refresh", false)) {
			if (EditText.class.isAssignableFrom(switchView.getClass())) {}
			else {
				toggleCheckList();
			}	
			prefs.edit().putBoolean("refresh", false).commit();		
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_toggle_checklist:
			toggleCheckList();
			break;

		case R.id.settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void toggleCheckList() {
		View newView;

		/*
		 * Here is where the job is done. By simply calling an instance of the
		 * ChecklistManager we can call its methods.
		 */
		try {
			// Getting instance
			ChecklistManager mChecklistManager = ChecklistManager
					.getInstance(mActivity);
			// Setting new entries hint text (if not set no hint
			// will be used)
			mChecklistManager.setNewEntryHint(prefs.getString("settings_hint", ""));
			// I want to make checks symbols visible when converting 
			// back to simple text from checklist
			mChecklistManager.setShowChecks(true);
			// Let checked items are moved on bottom
			mChecklistManager.setMoveCheckedOnBottom(Integer.valueOf(prefs.getString("settings_checked_items_behavior", "0")));
			// Converting actual EditText into a View that can
			// replace the source or viceversa
			newView = mChecklistManager.convert(switchView);
			// Replacing view in the layout
			mChecklistManager.replaceViews(switchView, newView);
			// Updating the instance of the pointed view for
			// eventual reverse conversion
			switchView = newView;
		} catch (ViewNotSupportedException e) {
			// This exception is fired if the source view class is
			// not supported
			e.printStackTrace();
		}
	}
}
