package it.feio.android.checklistview.interfaces;

import android.view.KeyEvent;
import it.feio.android.checklistview.models.CheckableLine;

public interface CheckListEventListener {
	/**
	 * This is called when an item checkbox is checked
	 * @param checkableLine
	 */
	public void onItemChecked(CheckableLine checkableLine);
	/**
	 * This is called when the checklist item is edited
	 * @param checkableLine
	 */
	public void onNewLineItemEdited(CheckableLine checkableLine);
	/**
	 * This is called when the ime action is performed (ex. next, done...)
	 * @param checkableLine
	 */
	public void onEditorActionPerformed(CheckableLine checkableLine, int actionId, KeyEvent event);
}
