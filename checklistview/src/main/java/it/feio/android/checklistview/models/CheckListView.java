package it.feio.android.checklistview.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import it.feio.android.checklistview.App;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.dragging.ChecklistViewItemOnDragListener;
import it.feio.android.checklistview.dragging.ChecklistViewOnTouchListener;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.widgets.EditTextMultiLineNoEnter;
import it.feio.android.pixlui.links.TextLinkClickListener;

@SuppressLint("NewApi")
public class CheckListView extends LinearLayout implements Constants, CheckListEventListener {

	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean showHintItem = Constants.SHOW_HINT_ITEM;
	private String newEntryHint = "";
	private int moveCheckedOnBottom = Settings.CHECKED_HOLD;

	private Context mContext;
	private CheckListChangedListener mCheckListChangedListener;
	private TextLinkClickListener mTextLinkClickListener;
	private ChecklistViewItemOnDragListener mChecklistViewItemOnDragListener;


	public CheckListView(Context activity) {
		super(activity);
		this.mContext = activity;
		setTag(Constants.TAG_LIST);
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		if (Build.VERSION.SDK_INT >= 11) {
			mChecklistViewItemOnDragListener = new ChecklistViewItemOnDragListener();
			this.setOnDragListener(mChecklistViewItemOnDragListener);
		}
	}


	/**
	 * Declare if a checked item must be moved on bottom of the list or not
	 */
	public void setMoveCheckedOnBottom(int moveCheckedOnBottom) {
		this.moveCheckedOnBottom = moveCheckedOnBottom;
	}


	/**
	 * Set if show or not a delete icon at the end of the line. Default true.
	 */
	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}


	/**
	 * Set if an empty line on bottom of the checklist must be shown or not
	 */
	public void setShowHintItem(boolean showHintItem) {
		this.showHintItem = showHintItem;
	}


	/**
	 * Text to be used as hint for the last empty line (hint item)
	 */
	public void setNewEntryHint(String hint) {
		setShowHintItem(true);
		this.newEntryHint = hint;
	}


	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).cloneStyles(v);
		}
	}


	public CheckListViewItem getChildAt(int i) {
		return (CheckListViewItem) super.getChildAt(i);
	}


	public EditText getEditText() {
		CheckListViewItem child = getChildAt(0);
		if (child != null) {
            return child.getEditText();
        }
		return null;
	}


	@Override
	public void onItemChecked(CheckListViewItem checked, boolean isChecked) {
        if (isChecked) {
            doOnCheck(checked);
        } else {
            doOnUncheck(checked);
        }
	}


    /**
     * If is not selected to HOLD checked items on position then the checked
     * item will be moved on bottom of the list
     */
    private void doOnCheck(CheckListViewItem checked) {
        if (moveCheckedOnBottom != Settings.CHECKED_HOLD) {
            for (int i = 0; i < getChildCount(); i++) {
                if (moveOnChecked(checked, i)) return;
            }
        }
        if (mCheckListChangedListener != null) mCheckListChangedListener.onCheckListChanged();
    }


    private boolean moveOnChecked(CheckListViewItem checked, int i) {
        if (checked.equals(getChildAt(i))) {

            // If it's on last position yet nothing will be done
            int lastIndex = getChildCount() - 1;
            if (i == lastIndex) {
                Log.v(Constants.TAG, "Not moving item it's the last one");
                return true;
            }

            // Otherwise all items at bottom than the actual will be
            // cycled until a good position is find.
            Log.v(Constants.TAG, "Moving item at position " + i);

            // The newly checked item will be positioned at last position.
            if (moveCheckedOnBottom == Settings.CHECKED_ON_BOTTOM) {
                removeView(checked);
                addView(checked, lastIndex);
                return true;
            }

            // Or at the top of checked ones
            if (moveCheckedOnBottom == Settings.CHECKED_ON_TOP_OF_CHECKED) {
                for (int j = lastIndex; j > i; j--) {
                    if (!getChildAt(j).isChecked()) {
                        removeView(checked);
                        addView(checked, j);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
    * Item has been unchecked and have to be (eventually) moved up
     */
    private void doOnUncheck(CheckListViewItem checked) {
        if (moveCheckedOnBottom != Settings.CHECKED_HOLD) {
            CheckListViewItem line;
            int position = 0;
            for (int i = 0; i < getChildCount(); i++) {
                line = getChildAt(i);
                position = i;
                if (line.isChecked() || line.isHintItem()) {
                    break;
                }
            }
            removeView(checked);
            addView(checked, position);
        }
    }


    @Override
	public void onNewLineItemEdited(CheckListViewItem checkableLine) {
		checkableLine.getCheckBox().setEnabled(true);
		enableDragAndDrop(checkableLine);
		addHintItem();
	}


	@Override
	public void onEditorActionPerformed(CheckListViewItem mCheckListViewItem, int actionId, KeyEvent event) {

		if (actionId != EditorInfo.IME_ACTION_NEXT && event.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            return;
        }

		EditTextMultiLineNoEnter v = mCheckListViewItem.getEditText();

		// Text length, start and end selection points, and other derived flags are retrieved
		int textLength = mCheckListViewItem.getText().length();
		int start = v.getSelectionStart();
		int end = v.getSelectionEnd();
		boolean isTextSelected = end != start;
		boolean isTruncating = !isTextSelected && start > 0 && start < textLength;
		int index = indexOfChild(mCheckListViewItem);
		int lastIndex = getChildCount() - 1;
		boolean isLastItem = index == lastIndex;

		// If the "next" ime key is pressed being into the hint item of the list the
		// softkeyboard will be hidden and focus assigned out of the checklist items.
		if (mCheckListViewItem.isHintItem() || isLastItem) {
			InputMethodManager inputManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(mCheckListViewItem.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			return;
		}

        CheckListViewItem nextItem = getChildAt(index + 1);

		if (newItemIsNeeded(textLength, start, isTextSelected, isTruncating, nextItem)) {
			nextItem.requestFocus();
			nextItem.getEditText().setSelection(0);
			return;
		}

		// The actual and the new one view contents are generated depending
		// on cursor position
		String text = v.getText().toString();
		String oldViewText = isTextSelected ? text.substring(0, start) + text.substring(end, text.length()) : text
				.substring(0, start);
		String newViewText = isTextSelected ? text.substring(start, end) : text.substring(end, text.length());

		// Actual view content is replaced
		v.setText(oldViewText);

		// A new checkable item is eventually created (optionally with text content)
		addItem(newViewText, mCheckListViewItem.isChecked(), index + 1);

		// The new view is focused
		getChildAt(index + 1).requestFocus();
	}


    /*
    * If line is empty a newline will not be created but the focus will be moved on bottom.
	* This must happen also if an empty line is already present under the actual one.
    */
    private boolean newItemIsNeeded(int textLength, int start, boolean isTextSelected, boolean isTruncating, CheckListViewItem nextItem) {
        boolean transposeSomeText = isTextSelected || isTruncating;
        return textLength == 0    // Empty line
                || (nextItem.getText().length() == 0 && !transposeSomeText)
                || (!transposeSomeText && start == 0);
    }


    /**
	 * Add a new item into the checklist
	 * 
	 * @param text
	 *            String to be inserted as item text
	 */
	public void addItem(String text) {
		addItem(text, false);
	}


	/**
	 * Add a new item into the checklist
	 * 
	 * @param text
	 *            String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked) {
		addItem(text, isChecked, null);
	}


	/**
	 * Add a new item into the checklist at specific index
	 * 
	 * @param text
	 *            String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked, Integer index) {
		CheckListViewItem mCheckListViewItem = new CheckListViewItem(mContext, isChecked, showDeleteIcon);
		mCheckListViewItem.cloneStyles(getEditText());
		mCheckListViewItem.setText(text);
		mCheckListViewItem.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mCheckListViewItem.setItemCheckedListener(this);
		// Links recognition
		if (mTextLinkClickListener != null) {
			mCheckListViewItem.getEditText().gatherLinksForText();
			mCheckListViewItem.getEditText().setOnTextLinkClickListener(mTextLinkClickListener);
		}
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckListViewItem.setCheckListChangedListener(this.mCheckListChangedListener);
		}
		if (index != null) {
			addView(mCheckListViewItem, index);
		} else {
			addView(mCheckListViewItem);
		}

		enableDragAndDrop(mCheckListViewItem);
	}


	private void enableDragAndDrop(CheckListViewItem mCheckListViewItem) {
		if (Build.VERSION.SDK_INT >= 11 && App.getSettings().getDragEnabled()) {
			mCheckListViewItem.getDragHandler().setOnTouchListener(new ChecklistViewOnTouchListener());
			mCheckListViewItem.setOnDragListener(mChecklistViewItemOnDragListener);
		}
	}


	/**
	 * Add a new item to the checklist
	 */
	public void addHintItem() {
		CheckListViewItem mCheckListViewItem = new CheckListViewItem(mContext, false, false);
		mCheckListViewItem.cloneStyles(getEditText());
		mCheckListViewItem.setHint(Html.fromHtml("<i>" + newEntryHint + "</i>"));
		mCheckListViewItem.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		// Set the checkbox initially disabled
		CheckBox c = mCheckListViewItem.getCheckBox();
		c.setEnabled(false);
		mCheckListViewItem.setCheckBox(c);
		// Attach listener
		mCheckListViewItem.setItemCheckedListener(this);
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckListViewItem.setCheckListChangedListener(this.mCheckListChangedListener);
		}

		// Defining position (default last, but if checked items behavior is not HOLD if changes)
		int hintItemPosition = getChildCount();
		if (moveCheckedOnBottom != Settings.CHECKED_HOLD) {
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildAt(i).isChecked()) {
					hintItemPosition = i;
					break;
				}
			}
		}

		// To avoid dropping here the dragged checklist items
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCheckListViewItem.setOnDragListener(new OnDragListener() {
				@Override
				public boolean onDrag(View v, DragEvent event) {
					if (event.getAction() == DragEvent.ACTION_DROP) {
						mChecklistViewItemOnDragListener.onDrag(v, event);
					}
					return true;
				}
			});
		}

		addView(mCheckListViewItem, hintItemPosition);
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


	@Override
	public void onLineDeleted(CheckListViewItem checkableLine) {
		mCheckListChangedListener.onCheckListChanged();
	}


	public void setOnTextLinkClickListener(TextLinkClickListener textlinkclicklistener) {
		mTextLinkClickListener = textlinkclicklistener;
	}

	
	@Override
	public boolean dispatchDragEvent(DragEvent ev) {
		boolean r = super.dispatchDragEvent(ev);
		if (r && (ev.getAction() == DragEvent.ACTION_DRAG_STARTED || ev.getAction() == DragEvent.ACTION_DRAG_ENDED)) {
			// If we got a start or end and the return value is true, our
			// onDragEvent wasn't called by ViewGroup.dispatchDragEvent
			// So we do it here.
			onDragEvent(ev);
		}
		return r;
	}

}
