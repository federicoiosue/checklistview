package it.feio.android.checklistview.test;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.R;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button b;
	View switchView;
	ChecklistManager mChecklistManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mChecklistManager = ChecklistManager.getInstance(this);
		mChecklistManager.setNewEntryText("mettiii");
		
		switchView = findViewById(R.id.edittext);
		
		b = (Button) findViewById(R.id.button);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View newView;
				try {
					newView = mChecklistManager.convert(switchView);
					mChecklistManager.replaceViews(switchView, newView);
					switchView = newView;
				} catch (ViewNotSupportedException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
