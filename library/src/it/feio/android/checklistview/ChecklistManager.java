package it.feio.android.checklistview;


import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckableLine;
import it.feio.android.checklistview.utils.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChecklistManager {
	
	private final String CARRIAGE_RETURN = System.getProperty("line.separator"); 

	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private boolean showChecks = Constants.SHOW_CHECKS;
	private boolean showHintItem = Constants.SHOW_HINT_ITEM;
	private String newEntryHint = "";	
	private int moveCheckedOnBottom = Constants.CHECKED_HOLD;

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

	/**
	 * Set if show or not a delete icon at the end of the line.
	 * Default true.
	 * @param showDeleteIcon True to show icon, false otherwise.
	 */
	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}

	/**
	 * Set if keep or remove checked items when converting back from 
	 * checklist to simple text.
	 * Default false.
	 * @param keepChecked True to keep checks, false otherwise.
	 */
	public void setKeepChecked(boolean keepChecked) {
		this.keepChecked = keepChecked;
	}

	/**
	 * Set if show checked or unchecked sequence symbols when converting back from 
	 * checklist to simple text.
	 * Default false.
	 * @param keepChecked True to keep checks, false otherwise.
	 */
	public void setShowChecks(boolean showChecks) {
		this.showChecks = showChecks;
	}
	

	public int getMoveCheckedOnBottom() {
		return moveCheckedOnBottom;
	}

	/**
	 * If set to true when an item is checked it is moved on bottom of the list
	 * @param moveCheckedOnBottom
	 */
	public void setMoveCheckedOnBottom(int moveCheckedOnBottom) {
		this.moveCheckedOnBottom = moveCheckedOnBottom;
	}
	
	/**
	 * Set if an empty line on bottom of the checklist must be shown or not
	 * @param showHintItem
	 */
	public void setShowHintItem(boolean showHintItem) {
		this.showHintItem= showHintItem;
	}

	/**
	 * Text to be used as hint for the last empty line (hint item)
	 * @param hint
	 */
	public String getNewEntryHint() {
		return newEntryHint;
	}

	/**
	 * Adds a new fillable line at the end of the checklist with hint text.
	 * Set an empty string to remove.
	 * @param newEntryHint Hint text
	 */
	public void setNewEntryHint(String newEntryHint) {
		setShowHintItem(true);
		this.newEntryHint = newEntryHint;
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
		mCheckListView.setMoveCheckedOnBottom(moveCheckedOnBottom);
		mCheckListView.setShowDeleteIcon(showDeleteIcon);
		mCheckListView.setNewEntryHint(newEntryHint);
		mCheckListView.setId(v.getId());
		
		String text = v.getText().toString();
		CheckableLine mCheckableLine;
		CheckBox mCheckBox;
		if (text.length() > 0) {
			String[] lines = text.split(CARRIAGE_RETURN);
	
			// All text lines will be cycled to build checklist items
			String lineText;
			boolean lineChecked = false;
			
			for (String line : lines) {
				
				if (line.length() == 0) 
					continue;
				
				// Line text content will be now stripped from checks symbols if they're
				// present (ex. [x] Task done -> lineText="Task done", lineChecked=true)
				lineChecked = line.indexOf(Constants.CHECKED_SYM) == 0;
				lineText = line.replace(Constants.CHECKED_SYM, "").replace(Constants.UNCHECKED_SYM, "");
				
				mCheckListView.addItem(lineText);
				
				if (lineChecked) {
					mCheckableLine = (CheckableLine) mCheckListView.getChildAt(mCheckListView.getChildCount() - 1);
					mCheckBox = mCheckableLine.getCheckBox();
					mCheckBox.setChecked(true);
					mCheckableLine.setCheckBox(mCheckBox);
				}
			}
		}
		
		// Add new fillable line if newEntryText has some text value
		if (showHintItem) {
			mCheckListView.addNewEmptyItem();
		}

		mCheckListView.cloneStyles(v);
		
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
		boolean isChecked;
		int childs = v.getChildCount() - (showHintItem ? 2 : 1); 
		for (int i = 0; i < childs; i++) {
			CheckableLine mCheckableLine = (CheckableLine) v.getChildAt(i);
			
			// If item is checked it will be removed if requested
			isChecked = mCheckableLine.isChecked();
			if ( !isChecked || (isChecked && keepChecked) ) { 
				sb	.append(i > 0 ? CARRIAGE_RETURN : "")
					.append(showChecks ? (isChecked ? Constants.CHECKED_SYM : Constants.UNCHECKED_SYM) : "")
					.append(mCheckableLine.getText());
			}
		}
		
		returnView.setText(sb.toString());
		returnView.setId(v.getId());
		
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
