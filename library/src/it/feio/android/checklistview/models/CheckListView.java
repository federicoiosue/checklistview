package it.feio.android.checklistview.models;

import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.interfaces.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CheckListView extends LinearLayout implements Constants, CheckListEventListener {
	
	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private boolean showChecks = Constants.SHOW_CHECKS;
	private boolean showHintItem = Constants.SHOW_HINT_ITEM;
	private String newEntryHint = "";
	private int moveCheckedOnBottom = Constants.CHECKED_HOLD;
	
	private Context mContext;
	private CheckListChangedListener mCheckListChangedListener;

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
	public void setNewEntryHint(String hint) {
		setShowHintItem(true);
		this.newEntryHint = hint;
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {		
		for (int i = 0; i < getChildCount(); i++) {
			((CheckListViewItem)getChildAt(i)).cloneStyles(v);
		}
	}
		
	
	
	/**
	 * Retrieve the edittext of a child line to be used to copy the typography
	 * @return
	 */
	public EditText getEditText() {
		EditText res = null;
		CheckListViewItem child = (CheckListViewItem)getChildAt(0);
		if (child != null)
			res = child.getEditText();
		return res;
	}

	
	@Override
	public void onItemChecked(CheckListViewItem checked, boolean isChecked) {
		if (isChecked) {
			// If is not selected to HOLD checked items on position then the checked
			// item will be moved on bottom of the list
			if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
				Log.v(Constants.TAG, "Moving checked on bottom");
				
				CheckListViewItem line;
				for (int i = 0; i < getChildCount(); i++) {
					
					line = ((CheckListViewItem)getChildAt(i));
					if (checked.equals(line)) {
						
						// If it's on last position yet nothing will be done
//						int lastIndex = showHintItem ? getChildCount() -2 : getChildCount() -1;
						int lastIndex = getChildCount() -1;
						if (i == lastIndex) {
							Log.v(Constants.TAG, "Not moving item it's the last one");		
							return;
						}
						
						// Otherwise all items at bottom than the actual will be 
						// cycled until a good position is find.
						Log.v(Constants.TAG, "Moving item at position " + i);
						CheckListViewItem lineAfter;
	
						// The newly checked item will be positioned at last position.
						if (moveCheckedOnBottom == Constants.CHECKED_ON_BOTTOM) {
							removeView(checked);
							addView(checked, lastIndex);
							return;
						}
							
						// Or at the top of checked ones
						if (moveCheckedOnBottom == Constants.CHECKED_ON_TOP_OF_CHECKED) {
							for (int j = lastIndex; j > i ; j--) {
								lineAfter = ((CheckListViewItem)getChildAt(j));
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
		// Item has been unchecked and have to be (eventually) moved up
		} else {
			if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
				Log.v(Constants.TAG, "Moving up item");
				
				CheckListViewItem line;
				int position = 0;
				for (int i = 0; i < getChildCount(); i++) {
					line = ((CheckListViewItem)getChildAt(i));
					position = i;
					if (line.isChecked()) break;
				}
				removeView(checked);
				addView(checked, position);
				
			}
		}
		
		// Notify something is changed 
		if (mCheckListChangedListener != null) {
			mCheckListChangedListener.onCheckListChanged();
		}
		
	}

	
	@Override
	public void onNewLineItemEdited(CheckListViewItem checkableLine) {
		addNewEmptyItem();
	}
	

	@Override
	public void onEditorActionPerformed(CheckListViewItem checkableLine, int actionId, KeyEvent event) {

		if (actionId != EditorInfo.IME_ACTION_NEXT)
			return;

		EditTextMultiLineNoEnter v = checkableLine.getEditText();

		// Start and end selection point are retrieved
		int start = v.getSelectionStart();
		int end = v.getSelectionEnd();

		// A check on the view position is done
		int index = indexOfChild(checkableLine);
		int lastIndex = showHintItem ? getChildCount() - 2 : getChildCount() - 1;
		boolean isLastItem = index == lastIndex;
		boolean isHintItem = index > lastIndex;
		
		// If the "next" ime key is pressed being into the hint item of the list the
		// softkeyboard will be hidden and focus assigned out of the checklist items.
		if (isHintItem) {
			InputMethodManager inputManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(checkableLine.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			return;
		}

		// The actual and the new one view contents are generated depending
		// on cursor position
		String text = v.getText().toString();
		boolean textSelected = end != start;
		String oldViewText = textSelected ? text.substring(0, start) + text.substring(end, text.length()) : text
				.substring(0, start);
		String newViewText = textSelected ? text.substring(start, end) : text.substring(end, text.length());

		// Actual view content is replaced
		v.setText(oldViewText);

		// A new checkable item is eventually created (optionally with text content)
		if (newViewText.length() > 0 || !isLastItem) {
			addItem(newViewText, checkableLine.isChecked(), index + 1);
		}

		// The new view is focused
		getChildAt(index + 1).requestFocus();
	}
	
	
	
	/**
	 * Add a new item into the checklist
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text){
		addItem(text, false);
	}
	
	
	
	/**
	 * Add a new item into the checklist
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked){
		addItem(text, isChecked, null);
	}
	
	
	/**
	 * Add a new item into the checklist at specific index
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked, Integer index){
		CheckListViewItem mCheckableLine = new CheckListViewItem(mContext, isChecked, showDeleteIcon);
		mCheckableLine.cloneStyles(getEditText());
		mCheckableLine.setText(text);
		mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mCheckableLine.setItemCheckedListener(this);		
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckableLine.setCheckListChangedListener(this.mCheckListChangedListener);
		}
		if (index != null) {
			addView(mCheckableLine, index);
		} else {
			addView(mCheckableLine);
		} 
	}
	
	
	/**
	 * Add a new item to the checklist 
	 * @param text String to be inserted as item text
	 */
	public void addNewEmptyItem(){
		CheckListViewItem mCheckableLine = new CheckListViewItem(mContext, false, false);
		mCheckableLine.cloneStyles(getEditText());
		mCheckableLine.setHint(newEntryHint);
		mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		// Set the checkbox initially disabled
		CheckBox c = mCheckableLine.getCheckBox();
		c.setEnabled(false);
		mCheckableLine.setCheckBox(c);
		// Attach listener
		mCheckableLine.setItemCheckedListener(this);		
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckableLine.setCheckListChangedListener(this.mCheckListChangedListener);
		}
		// Add view
		addView(mCheckableLine);
	}
	




	private void focusView(View v, int focusDirection) {
		EditTextMultiLineNoEnter focusableEditText = (EditTextMultiLineNoEnter) v.focusSearch(focusDirection);
		if (focusableEditText != null) {
			focusableEditText.requestFocus();
			focusableEditText.setSelection(focusableEditText.getText().length());
		}		
	}
	
	
	
	
	public void setCheckListChangedListener(CheckListChangedListener mCheckListChangedListener) {
		this.mCheckListChangedListener = mCheckListChangedListener;
	}

//	@Override
//	public void afterTextChanged(Editable s) {}
//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {}
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before, int count) {		
//		// Eventually notify something is changed 
//		if (mCheckListChangedListener != null) {
//			mCheckListChangedListener.onCheckListChanged();
//		}		
//	}

	@Override
	public void onLineDeleted(CheckListViewItem checkableLine) {
		// Eventually notify something is changed 
		mCheckListChangedListener.onCheckListChanged();
	}

}
