package it.feio.android.checklistview.models;

import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.utils.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CheckListView extends LinearLayout implements Constants, CheckListEventListener {
	
	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private int moveCheckedOnBottom = Constants.CHECKED_HOLD;
	private boolean newItem = Constants.NEW_ITEM;
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
	public void setMoveCheckedOnBottom(int moveCheckedOnBottom) {
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
		if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
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
					
					// Otherwise all items at bottom than the actual will be 
					// cycled until a good position is find.
					Log.v(Constants.TAG, "Moving item at position " + i);
					CheckableLine lineAfter;

					// The newly checked item will be positioned at last position.
					if (moveCheckedOnBottom == Constants.CHECKED_ON_BOTTOM) {
						removeView(checked);
						addView(checked, lastIndex);
						return;
					}
						
					// Or at the top of checked ones
					if (moveCheckedOnBottom == Constants.CHECKED_ON_TOP_OF_CHECKED) {
						for (int j = lastIndex; j > i ; j--) {
							lineAfter = ((CheckableLine)getChildAt(j));
							if (!lineAfter.isChecked()) {
								removeView(checked);
								addView(checked, j);
								return;
							} 
						}
					}
				}
			}
		}		
	}

	@Override
	public void onNewLineItemEdited(CheckableLine checkableLine) {
		CheckableLine mCheckableLine = new CheckableLine(mContext, false);
		mCheckableLine.cloneStyles(getEditText());
		mCheckableLine.setHint(newEntryHint);
		mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		CheckBox c = mCheckableLine.getCheckBox();
		c.setEnabled(false);
		mCheckableLine.setCheckBox(c);	
		mCheckableLine.setItemCheckedListener(this);
		addView(mCheckableLine);
	}
	
	
	/**
	 * Add a new item to the checklist
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text){
		CheckableLine mCheckableLine = new CheckableLine(mContext, showDeleteIcon);
		mCheckableLine.setText(text);
		mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mCheckableLine.setItemCheckedListener(this);
		addView(mCheckableLine);
	}
	
	
	/**
	 * Add a new item to the checklist
	 * @param text String to be inserted as item text
	 */
	public void addNewEmptyItem(){
		CheckableLine mCheckableLine = new CheckableLine(mContext, false);
		mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mCheckableLine.setHint(newEntryHint);
		mCheckableLine.setItemCheckedListener(this);
		addView(mCheckableLine);
	}

	

}
