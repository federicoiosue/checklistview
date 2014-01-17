package it.feio.android.checklistview;

import com.neopixl.pixlui.components.edittext.EditText;

import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckableLine;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

public class ChecklistManager {
	
	private final String CARRIAGE_RETURN = System.getProperty("line.separator"); 
	private final String UNCHECKED = "[ ]";
	private final String CHECKED = "[x]";

	private boolean showDeleteIcon = true;
	private boolean keepChecked = false;
	private String newEntryText = "";	

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

	public boolean getShowDeleteIcon() {
		return showDeleteIcon;
	}

	/**
	 * Set if show or not a delete icon at the end of the line.
	 * Default true.
	 * @param showDeleteIcon True to show icon, false otherwise.
	 */
	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}

	public boolean getKeepChecked() {
		return keepChecked;
	}

	/**
	 * Set if show checked or unchedes sequence symbols when converting back from 
	 * checklist to edittext.
	 * Default false.
	 * @param keepChecked True to keep checks, false otherwise.
	 */
	public void setKeepChecked(boolean keepChecked) {
		this.keepChecked = keepChecked;
	}

	public String getNewEntryText() {
		return newEntryText;
	}

	/**
	 * Adds a new fillable line at the end of the checklist with hint text.
	 * Set an empty string to remove.
	 * @param newEntryText Hint text
	 */
	public void setNewEntryText(String newEntryText) {
		this.newEntryText = newEntryText;
	}

	public View convert(View v) throws ViewNotSupportedException {
		if (EditText.class.isAssignableFrom(v.getClass())) {
			return convert((EditText)v);
		} else if (LinearLayout.class.isAssignableFrom(v.getClass())) {
			return convert((CheckListView) v);
		} else {
			return null;
		}
	}

	
	/**
	 * Conversion from EditText to checklist
	 * @param v EditText view
	 * @return converted view to replace
	 */
	private View convert(EditText v) {

		CheckListView mCheckListView = new CheckListView(mActivity);

		String text = v.getText().toString();

		CheckableLine mCheckableLine;
		if (text.length() > 0) {
			String[] lines = text.split(CARRIAGE_RETURN);
	
			for (String line : lines) {
				if (line.length() == 0) continue;
				mCheckableLine = new CheckableLine(mActivity, showDeleteIcon);
				mCheckableLine.setText(line);
				mCheckListView.addView(mCheckableLine);
			}
		}
		
		// Add new fillable line if newEntryText has some text value
		if (newEntryText.length() > 0) {
			mCheckableLine = new CheckableLine(mActivity, false);
			mCheckableLine.setHint(newEntryText);
			mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
			mCheckListView.addView(mCheckableLine);
			mCheckableLine.cloneStyles(v);
		}

		mCheckListView.cloneStyles(v);
//		mCheckListView.setLayoutParams(v.getLayoutParams());
		
		return mCheckListView;
	}
	
	
	
	/**
	 * Conversion from checklist view to EditText
	 * @param v CheckListView to be re-converted
	 * @return EditText
	 */
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
		
		// Restoring the typography
		returnView.setTypeface(v.getEditText().getTypeface());
		
		return returnView;
	}

	
	/**
	 * Replace a 
	 * @param oldView
	 * @param newView
	 */
	public void replaceViews(View oldView, View newView) {
		if (oldView == null || newView == null)
			return;
			
		ViewGroup parent = (ViewGroup) oldView.getParent();
		int index = parent.indexOfChild(oldView);
		parent.removeView(oldView);
		parent.addView(newView, index);
	}
	
}
