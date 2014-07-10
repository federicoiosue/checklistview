package it.feio.android.checklistview.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.Constants;

public class MainActivity extends Activity implements CheckListChangedListener {

	Button b;
	View switchView;
	private SharedPreferences prefs;
	boolean isChecklist;
	private ChecklistManager mChecklistManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		isChecklist = prefs.getBoolean("isChecklist", false);

		switchView = findViewById(R.id.edittext);		
		((EditText)switchView).setText(prefs.getString("text", getString(R.string.template_phrase)));
		
		if (isChecklist) {
			isChecklist = false;
			toggleCheckList();
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("refresh", false)) {
			if (isChecklist) {
			} else {
				toggleCheckList();
			}
			prefs.edit().putBoolean("refresh", false).commit();
		}
	}
	
	
	@Override
	protected void onPause() {
		save();
		super.onPause();
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
			mChecklistManager = ChecklistManager.getInstance(getApplicationContext());

			/*
			 * These method are useful when converting from EditText to
			 * ChecklistView (but can be set anytime, they'll be used at
			 * appropriate moment)
			 */

			// Setting new entries hint text (if not set no hint
			// will be used)
			mChecklistManager.setNewEntryHint(prefs.getString("settings_hint", ""));
			// Let checked items are moved on bottom
			
			mChecklistManager.setMoveCheckedOnBottom(Integer.valueOf(prefs.getString("settings_checked_items_behavior",
					String.valueOf(Settings.CHECKED_HOLD))));
			
			// Is also possible to set a general changes listener
			mChecklistManager.setCheckListChangedListener(this);

			
			/*
			 * These method are useful when converting from ChecklistView to
			 * EditText (but can be set anytime, they'll be used at appropriate
			 * moment)
			 */

			// Decide if keep or remove checked items when converting
			// back to simple text from checklist
			mChecklistManager.setLinesSeparator(prefs.getString("settings_lines_separator", Constants.LINES_SEPARATOR));
			
			// Decide if keep or remove checked items when converting
			// back to simple text from checklist
			mChecklistManager.setKeepChecked(prefs.getBoolean("settings_keep_checked", Constants.KEEP_CHECKED));
			
			// I want to make checks symbols visible when converting
			// back to simple text from checklist
			mChecklistManager.setShowChecks(prefs.getBoolean("settings_show_checks", Constants.SHOW_CHECKS));

			// Enable or disable drag & drop
//			mChecklistManager.setDragEnabled(false);
			mChecklistManager.setDragVibrationEnabled(true);
			
			// Converting actual EditText into a View that can
			// replace the source or viceversa
			newView = mChecklistManager.convert(switchView);
			
			// Replacing view in the layout
			mChecklistManager.replaceViews(switchView, newView);
			
			// Updating the instance of the pointed view for
			// eventual reverse conversion
			switchView = newView;
						
			isChecklist = !isChecklist;
		
		} catch (ViewNotSupportedException e) {
			// This exception is fired if the source view class is
			// not supported
			e.printStackTrace();
		}
	}
	
	
	
	private void save(){
		String text = "";
		if (isChecklist) {
			try {
				text = ((EditText)mChecklistManager.convert(switchView)).getText().toString();
			} catch (ViewNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			text = ((EditText)switchView).getText().toString();
		}
		prefs.edit()
			.putString("text", text)
			.putBoolean("isChecklist", isChecklist)
			.commit();
	}

	

	@Override
	public void onCheckListChanged() {
		Log.v(Constants.TAG, "Some text is changed!!");
	}
}
