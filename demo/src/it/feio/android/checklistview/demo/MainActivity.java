package it.feio.android.checklistview.demo;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private final String HINT = "New line...";

	Button b;
	View switchView;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mActivity = this;
		switchView = findViewById(R.id.edittext);

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

		default:
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
			mChecklistManager.setNewEntryHint(HINT);
			// Let checked items are moved on bottom
			mChecklistManager.setMoveCheckedOnBottom(true);
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
