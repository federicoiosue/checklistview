package it.feio.android.checklist;

import com.example.checklist.exceptions.ViewNotSupportedException;
import com.example.checklist.models.CheckListView;
import com.example.checklist.models.CheckableLine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChecklistManager {
	
	private final String CARRIAGE_RETURN = System.getProperty("line.separator"); 
	private final String UNCHECKED = "[ ]";
	private final String CHECKED = "[x]";
	private boolean keepChecked = false;

	private static ChecklistManager instance = null;
	private Activity mActivity;
	private ChecklistManager(Activity mActivity) {
		this.mActivity = mActivity;
		mActivity.getLayoutInflater();
	}

	public static synchronized ChecklistManager getInstance(Activity mActivity) {
		if (instance == null) {
			instance = new ChecklistManager(mActivity);
		}
		return instance;
	}

	public boolean getKeepChecked() {
		return keepChecked;
	}

	public void setKeepChecked(boolean keepChecked) {
		this.keepChecked = keepChecked;
	}
	
	
	
	public View convert(View v) throws ViewNotSupportedException {
		if (TextView.class.isAssignableFrom(v.getClass())) {
			return convert((TextView)v);
		} else if (LinearLayout.class.isAssignableFrom(v.getClass())) {
			return convert((CheckListView) v);
		} else {
			return null;
		}
	}

	
	private View convert(TextView v) {

		CheckListView mCheckListView = new CheckListView(mActivity);

		String text = v.getText().toString();
		String[] lines = text.split(CARRIAGE_RETURN);

		CheckableLine mCheckableLine;
		for (String line : lines) {
			mCheckableLine = new CheckableLine(mActivity);
			mCheckableLine.setText(line);
			mCheckListView.addView(mCheckableLine);
		}

		mCheckListView.cloneBackground(v.getBackground());
		
		return mCheckListView;
	}

	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private View convert(CheckListView v) {
		EditText returnView = new EditText(mActivity);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < v.getChildCount(); i++) {
			CheckableLine mCheckableLine = (CheckableLine) v.getChildAt(i);
			sb	.append(i > 0 ? CARRIAGE_RETURN : "")
				.append(keepChecked ? (mCheckableLine.isChecked() ? CHECKED : UNCHECKED) : "")
				.append(mCheckableLine.getText());
		}
		
		returnView.setText(sb.toString());
		
		if (Build.VERSION.SDK_INT < 16) {
			returnView.setBackgroundDrawable(v.getBackground());
		} else {
			returnView.setBackground(v.getBackground());
		}
		
		return returnView;
	}

	
	public void replaceViews(View oldView, View newView) {
		if (oldView == null || newView == null)
			return;
			
		ViewGroup parent = (ViewGroup) oldView.getParent();
		int index = parent.indexOfChild(oldView);
		parent.removeView(oldView);
		parent.addView(newView, index);
	}
	
}
