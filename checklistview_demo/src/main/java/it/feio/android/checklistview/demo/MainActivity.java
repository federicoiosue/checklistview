package it.feio.android.checklistview.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.Constants;

public class MainActivity extends AppCompatActivity implements CheckListChangedListener {

	View switchView;
	private SharedPreferences prefs;
	boolean isChecklist;
	private ChecklistManager mChecklistManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		switchView = findViewById(R.id.edittext);		
		((EditText)switchView).setText(prefs.getString("text", getString(R.string.template_phrase)));
		
		if (prefs.getBoolean("isChecklist", false)) {
			isChecklist = false;
			toggleCheckList();
		}

		initTextView();
	}


	private void initTextView() {
		TextView textview = (TextView) findViewById(R.id.bottom_banner);
		textview.setText(Html.fromHtml(getString(R.string.omninotes)));
		textview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.omninotes_link))));
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("refresh", false)) {
			if (!isChecklist) {
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_toggle_checklist:
				toggleCheckList();
				break;

			case R.id.settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
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
			mChecklistManager.newEntryHint(prefs.getString("settings_hint", ""));
			// Let checked items are moved on bottom
			
			mChecklistManager.moveCheckedOnBottom(Integer.valueOf(prefs.getString("settings_checked_items_behavior",
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
			mChecklistManager.linesSeparator(prefs.getString("settings_lines_separator", Constants.LINES_SEPARATOR));
			
			// Decide if keep or remove checked items when converting
			// back to simple text from checklist
			mChecklistManager.keepChecked(prefs.getBoolean("settings_keep_checked", Constants.KEEP_CHECKED));
			
			// I want to make checks symbols visible when converting
			// back to simple text from checklist
			mChecklistManager.showCheckMarks(prefs.getBoolean("settings_show_checks", Constants.SHOW_CHECKS));

			// Enable or disable drag & drop
			mChecklistManager.dragEnabled(true);
			mChecklistManager.dragVibrationEnabled(true);
			
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
			// This exception is fired if the source view class is not supported
			e.printStackTrace();
		}
	}
	

	private void save(){
		String text = "";
		if (isChecklist) {
			try {
				text = ((EditText)mChecklistManager.convert(switchView)).getText().toString();
			} catch (ViewNotSupportedException e) {
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
