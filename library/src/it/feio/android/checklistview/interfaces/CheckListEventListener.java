package it.feio.android.checklistview.interfaces;

import android.view.KeyEvent;
import it.feio.android.checklistview.models.CheckListViewItem;

public interface CheckListEventListener {
	/**
	 * This is called when an item checkbox is checked
	 * @param checkableLine
	 */
	public void onItemChecked(CheckListViewItem checkableLine, boolean isChecked);
	/**
	 * This is called when the checklist item is edited
	 * @param checkableLine
	 */
	public void onNewLineItemEdited(CheckListViewItem checkableLine);
	/**
	 * This is called when the ime action is performed (ex. next, done...)
	 * @param checkableLine
	 */
	public void onEditorActionPerformed(CheckListViewItem checkableLine, int actionId, KeyEvent event);
	/**
	 * This is called when the ime action is performed (ex. next, done...)
	 * @param checkableLine
	 */
	public void onLineDeleted(CheckListViewItem checkableLine);
}
