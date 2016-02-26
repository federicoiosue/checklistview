package it.feio.android.checklistview.interfaces;

import it.feio.android.checklistview.models.CheckListViewItem;
import android.view.KeyEvent;

public interface CheckListEventListener {
	/**
	 * This is called when an item checkbox is checked.
	 * @param checkableLine
	 */
	void onItemChecked(CheckListViewItem checkableLine, boolean isChecked);
	/**
	 * This is called when the checklist item is edited
	 * @param checkableLine
	 */
	void onNewLineItemEdited(CheckListViewItem checkableLine);
	/**
	 * This is called when the ime action is performed (ex. next, done...)
	 * @param checkableLine
	 */
	void onEditorActionPerformed(CheckListViewItem checkableLine, int actionId, KeyEvent event);
	/**
	 * This is called when the ime action is performed (ex. next, done...)
	 * @param checkableLine
	 */
	void onLineDeleted(CheckListViewItem checkableLine);
}
