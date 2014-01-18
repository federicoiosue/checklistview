package it.feio.android.checklistview.models;

import it.feio.android.checklistview.interfaces.ItemCheckedListener;
import it.feio.android.checklistview.utils.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CheckListView extends LinearLayout implements ItemCheckedListener {

//	ArrayList<CheckableLine> lines = new ArrayList<CheckableLine>();
	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private boolean moveCheckedOnBottom = Constants.MOVE_CHECKED_ON_BOTTOM;
	private String newEntryHint = "";
	
	private Context mContext;

	public CheckListView(Context context) {
		super(context);
		this.mContext = context;
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Declare if a checked item must be moved on bottom of the list or not
	 * @param moveCheckedOnBottom
	 */
	public void setMoveCheckedOnBottom(boolean moveCheckedOnBottom) {
		this.moveCheckedOnBottom = moveCheckedOnBottom;
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
	 * If its not an empty string it will be used as hint for the last empty line
	 * @param hint
	 */
	public void setNewEntryHint(String hint) {
		this.newEntryHint = hint;
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {		
		for (int i = 0; i < getChildCount(); i++) {
			((CheckableLine)getChildAt(i)).cloneStyles(v);
		}
	}
		
	
	
	/**
	 * Retrieve the edittext of a child line to be used to copy the typography
	 * @return
	 */
	public EditText getEditText() {
		EditText res = null;
		CheckableLine child = (CheckableLine)getChildAt(0);
		if (child != null)
			res = child.getEditText();
		return res;
	}

	@Override
	public void onItemChecked(CheckableLine checked) {
		// If moveCheckedOnBottom is true the checked item will be moved on bottom of the list
		if (moveCheckedOnBottom) {
			Log.v(Constants.TAG, "Moving checked on bottom");
			CheckableLine line;
			for (int i = 0; i < getChildCount(); i++) {
				line = ((CheckableLine)getChildAt(i));
				if (checked.equals(line)) {
					
					// If it's on last position yet nothing will be done
					int lastIndex = newEntryHint != "" ? getChildCount() -2 : getChildCount() -1;
					if (i == lastIndex) {
						Log.v(Constants.TAG, "Not moving item it's the last one");		
						return;
					}
					
					// The checked view is removed from actual position
					removeView(checked);
					
					// Otherwise all items at bottom than the actual will be 
					// cycled until a good position is find.
					Log.v(Constants.TAG, "Moving item at position " + i);
					CheckableLine lineAfter;
					// Starting from the end all item will be shifted of one position 
					// down until a non checked is found. The newly checked item will be
					// positioned next to this.
					for (int j = lastIndex; j > i ; j--) {
						lineAfter = ((CheckableLine)getChildAt(j));
						if (!lineAfter.isChecked()) {
							addView(checked, j);
						} 
					}
				}
			}
		}		
	}
	
	
	/**
	 * Add a new line item to che checklist
	 * @param text String to be inserted as item text
	 */
	public void addNewLine(String text){
		CheckableLine mCheckableLine = new CheckableLine(mContext, showDeleteIcon);
		mCheckableLine.setText(text);
		mCheckableLine.setItemCheckedListener(this);
		addView(mCheckableLine);
	}

	

}
