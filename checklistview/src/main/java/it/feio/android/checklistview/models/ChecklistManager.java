package it.feio.android.checklistview.models;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import it.feio.android.checklistview.App;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.pixlui.links.TextLinkClickListener;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;


public class ChecklistManager {

	private WeakReference<Context> mContext;
	private TextWatcher mTextWatcher;
	private CheckListChangedListener mCheckListChangedListener;
	private CheckListView mCheckListView;
	private TextLinkClickListener mTextLinkClickListener;
	private EditText originalView;
	private View undoBarContainerView;
	private boolean undoBarEnabled = true;


	public ChecklistManager(Context context) {
		this.mContext = new WeakReference<>(context);
	}


	/**
	 * Set the string to be used to split initial text into checklist items. Default System line separator (carriage
	 * return).
	 *
	 * @param linesSeparator String separator
	 */
	public ChecklistManager linesSeparator(String linesSeparator) {
		App.getSettings().setLinesSeparator(linesSeparator.length() == 0 ? Constants.LINES_SEPARATOR : linesSeparator);
		return this;
	}


	/**
	 * Set if show or not a delete icon at the end of the line. Default true.
	 *
	 * @param showDeleteIcon True to show icon, false otherwise.
	 */
	public ChecklistManager showDeleteIcon(boolean showDeleteIcon) {
		App.getSettings().setShowDeleteIcon(showDeleteIcon);
		return this;
	}


	/**
	 * Set if keep or remove checked items when converting back from checklist to simple text. Default false.
	 *
	 * @param keepChecked True to keep checks, false otherwise.
	 */
	public ChecklistManager keepChecked(boolean keepChecked) {
		App.getSettings().setKeepChecked(keepChecked);
		return this;
	}


	/**
	 * Set if show checked or unchecked sequence symbols when converting back from checklist to simple text. Default
	 * false.
	 */
	public ChecklistManager showCheckMarks(boolean showChecks) {
		App.getSettings().setShowChecks(showChecks);
		return this;
	}


	public int getMoveCheckedOnBottom() {
		return App.getSettings().getMoveCheckedOnBottom();
	}


	/**
	 * If set to true when an item is checked it is moved on bottom of the list
	 *
	 * @param moveCheckedOnBottom
	 */
	public ChecklistManager moveCheckedOnBottom(int moveCheckedOnBottom) {
		App.getSettings().setMoveCheckedOnBottom(moveCheckedOnBottom);
		return this;
	}


	/**
	 * Set if an empty line on bottom of the checklist must be shown or not
	 *
	 * @param showHintItem
	 */
	public ChecklistManager showHintItem(boolean showHintItem) {
		App.getSettings().setShowHintItem(showHintItem);
		return this;
	}


	/**
	 * Text to be used as hint for the last empty line (hint item)
	 */
	public String getNewEntryHint() {
		return App.getSettings().getNewEntryHint();
	}


	/**
	 * Adds a new fillable line at the end of the checklist with hint text. Set an empty string to remove.
	 *
	 * @param newEntryHint Hint text
	 */
	public ChecklistManager newEntryHint(String newEntryHint) {
		showHintItem(true);
		App.getSettings().setNewEntryHint(newEntryHint);
		return this;
	}


	public ChecklistManager dragEnabled(boolean dragEnabled) {
		App.getSettings().setDragEnabled(dragEnabled);
		return this;
	}


	public boolean getDragEnabled() {
		return App.getSettings().getDragEnabled();
	}


	public ChecklistManager dragVibrationEnabled(boolean dragVibrationEnabled) {
		App.getSettings().setDragVibrationEnabled(dragVibrationEnabled);
		return this;
	}


	public boolean getDragVibrationEnabled() {
		return App.getSettings().getDragVibrationEnabled();
	}


	public ChecklistManager dragVibrationDuration(int dragVibrationDuration) {
		App.getSettings().setDragVibrationDuration(dragVibrationDuration);
		return this;

	}


	public int getDragVibrationDuration() {
		return App.getSettings().getDragVibrationDuration();
	}


	/**
	 * Disable undo snackbar
	 */
	public ChecklistManager disableUndoBar() {
		this.undoBarEnabled= false;
		return this;
	}


	/**
	 * Used to set a custom View to contain item undo deletion SnackBar
	 * @param undoBarContainerView Container view
	 */
	public ChecklistManager undoBarContainerView(final View undoBarContainerView) {
		this.undoBarContainerView = undoBarContainerView;
		return this;
	}


	public View convert(View v) throws ViewNotSupportedException {
		if (EditText.class.isAssignableFrom(v.getClass())) {
			return convert((EditText) v);
		} else if (LinearLayout.class.isAssignableFrom(v.getClass())) {
			return convert((CheckListView) v);
		} else {
			return null;
		}
	}


	/**
	 * Conversion from EditText to checklist
	 *
	 * @param v EditText view
	 * @return converted view to replace
	 */
	private View convert(EditText v) {

		this.originalView = v;
		mCheckListView = new CheckListView(mContext);
		mCheckListView.setMoveCheckedOnBottom(App.getSettings().getMoveCheckedOnBottom());
		mCheckListView.setUndoBarEnabled(undoBarEnabled);
		mCheckListView.setUndoBarContainerView(undoBarContainerView);
		mCheckListView.setShowDeleteIcon(App.getSettings().getShowDeleteIcon());
		mCheckListView.setNewEntryHint(App.getSettings().getNewEntryHint());
		mCheckListView.setId(v.getId());

		// Listener for general event is propagated on bottom
		if (mCheckListChangedListener != null) {
			mCheckListView.setCheckListChangedListener(mCheckListChangedListener);
		}

		// Listener for clicks on links
		if (mTextLinkClickListener != null) {
			mCheckListView.setOnTextLinkClickListener(mTextLinkClickListener);
		}

		String text = v.getText().toString();

		// Parse all lines if text is not empty
		convertToChecklist(text);

		// Add new fillable line if newEntryText has some text value or showHintItem is set to true
		if (App.getSettings().getShowHintItem()) {
			mCheckListView.addHintItem();
		}

		mCheckListView.cloneStyles(v);

		return mCheckListView;
	}


	private void convertToChecklist(String text) {
		if (text.length() > 0) {
			for (String line : text.split(Pattern.quote(App.getSettings().getLinesSeparator()))) {
				convertLineToChecklist(line);
			}
		}
	}


	private void convertLineToChecklist(String line) {
		if (line.length() == 0) {
			return;
		}
		// Line text content will be now stripped from checks symbols if they're present
		// (ex. [x] Task done -> lineText="Task done", lineChecked=true)
		boolean isChecked = line.indexOf(Constants.CHECKED_SYM) == 0;
		String lineText = line.replace(Constants.CHECKED_SYM, "").replace(Constants.UNCHECKED_SYM, "");
		mCheckListView.addItem(lineText, isChecked);
	}


	/**
	 * Conversion from checklist view to EditText
	 *
	 * @param v CheckListView to be re-converted
	 * @return EditText
	 */
	private View convert(CheckListView v) {

		StringBuilder sb = new StringBuilder();
		removeChecked(v, sb);
		originalView.setText(sb.toString());

		// Associating textChangedListener
		if (this.mTextWatcher != null) {
			originalView.addTextChangedListener(this.mTextWatcher);
		}

		// Reset to null the field
		mCheckListView = null;

		return originalView;
	}


	private void restoreTypography(CheckListView v, EditText returnView) {
		if (v.getEditText() != null) {
			returnView.setTypeface(v.getEditText().getTypeface());
			returnView.setTextSize(0, v.getEditText().getTextSize());
			returnView.setTextColor(v.getEditText().getTextColors());
			returnView.setLinkTextColor(v.getEditText().getLinkTextColors());
		}
	}


	private void removeChecked(CheckListView v, StringBuilder sb) {
		boolean isChecked;
		for (int i = 0; i < v.getChildCount(); i++) {
			CheckListViewItem mCheckListViewItem = v.getChildAt(i);
			if (mCheckListViewItem.isHintItem()) {
				continue;
			}
			// If item is checked it will be removed if requested
			isChecked = mCheckListViewItem.isChecked();
			if (!isChecked || (isChecked && App.getSettings().getKeepChecked())) {
				sb.append(i > 0 ? App.getSettings().getLinesSeparator() : "")
						.append(App.getSettings().getShowChecks() ? isChecked ? Constants.CHECKED_SYM
								: Constants.UNCHECKED_SYM : "").append(mCheckListViewItem.getText());
			}
		}
	}


	/**
	 * Replace a view with another
	 */
	public void replaceViews(View oldView, View newView) {
		if (oldView != null && newView != null) {
			ViewGroup parent = (ViewGroup) oldView.getParent();
			int index = parent.indexOfChild(oldView);
			parent.removeView(oldView);
			parent.addView(newView, index);
		}
	}


	public void setCheckListChangedListener(CheckListChangedListener mCheckListChangedListener) {
		this.mCheckListChangedListener = mCheckListChangedListener;
	}


	public void addTextChangedListener(TextWatcher mTextWatcher) {
		this.mTextWatcher = mTextWatcher;
	}


	public String getText() {
		if (mCheckListView == null) {
			return "";
		}
		StringBuilder stringbuilder = new StringBuilder();
		int i = 0;
		do {
			CheckListViewItem checklistviewitem;
			if (i >= mCheckListView.getChildCount()) {
				if (stringbuilder.length() > App.getSettings().getLinesSeparator().length()) {
					return stringbuilder.substring(App.getSettings().getLinesSeparator().length());
				}
				return "";
			}
			checklistviewitem = mCheckListView.getChildAt(i);
			if (!checklistviewitem.isHintItem()) {
				boolean flag = checklistviewitem.isChecked();
				if (!flag || flag && App.getSettings().getKeepChecked()) {
					StringBuilder stringbuilder1 = stringbuilder.append(App.getSettings().getLinesSeparator());
					String s = "";
					if (App.getSettings().getShowChecks()) {
						s = flag ? Constants.CHECKED_SYM : Constants.UNCHECKED_SYM;
					}
					stringbuilder1.append(s).append(checklistviewitem.getText());
				}
			}
			i++;
		} while (true);
	}


	/**
	 * Counts the number of checked items in the list
	 */
	public int getCheckedCount() {
		int count = 0;
		if (mCheckListView != null) {
			for (int i = 0; i < mCheckListView.getChildCount(); i++) {
				CheckListViewItem mCheckListViewItem = mCheckListView.getChildAt(i);
				if (!mCheckListViewItem.isHintItem() && mCheckListViewItem.isChecked()) {
					count++;
				}
			}
		}
		return count;
	}


	/**
	 * Counts the number of items excluding the hint item
	 */
	public int getCount() {
		int count = 0;
		if (mCheckListView != null) {
			for (int i = 0; i < mCheckListView.getChildCount(); i++) {
				if (!mCheckListView.getChildAt(i).isHintItem()) {
					count++;
				}
			}
		}
		return count;
	}


	/**
	 * Returns the eventually focused item in the list
	 */
	public CheckListViewItem getFocusedItemView() {
		if (mCheckListView != null && mCheckListView.hasFocus()) {
			for (int i = 0; i < mCheckListView.getChildCount(); i++) {
				if (mCheckListView.getChildAt(i).hasFocus()) {
					return mCheckListView.getChildAt(i);
				}
			}
		}
		return null;
	}


	public void setOnTextLinkClickListener(TextLinkClickListener textlinkclicklistener) {
		mTextLinkClickListener = textlinkclicklistener;
	}

}
