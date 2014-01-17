CheckListView
=============

Library to convert an EditText into a View capable of acting as checklist


Usage
============

```java
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
		
		b = (Button) findViewById(R.id.button);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View newView;
				
				/*
				 * Here is where the job is done.
				 * By simply calling an instance of the ChecklistManager we can call its methods.	 
				 */
				try {
					// Getting instance
					ChecklistManager mChecklistManager = ChecklistManager.getInstance(mActivity);
					// Setting new entries hint text (if not set no hint will be used)
					mChecklistManager.setNewEntryText(HINT);
					// Converting actual EditText into a View that can replace the source or viceversa
					newView = mChecklistManager.convert(switchView);
					// Replacing view in the layout
					mChecklistManager.replaceViews(switchView, newView);
					// Updating the instance of the pointed view for eventual reverse conversion
					switchView = newView;
				} catch (ViewNotSupportedException e) {
					// This exception is fired if the source view class is not supported
					e.printStackTrace();
				}
			}
		});		
	}
	
}
```

Developed By
============

* Federico Iosue - <federico.iosue@gmail.com>


License
=======

    Copyright 2013 Federico Iosue

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
