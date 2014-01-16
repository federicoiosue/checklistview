package com.example.checklist.test;

import it.feio.android.checklist.ChecklistManager;

import com.example.checklist.R;
import com.example.checklist.exceptions.ViewNotSupportedException;

import android.os.Bundle;
import android.app.Activity;
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
