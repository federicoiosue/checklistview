package it.feio.android.checklistview.interfaces;

import it.feio.android.checklistview.models.CheckableLine;

public interface CheckListEventListener {
	/**
	 * This is called when an item checkbox is checked
	 * @param checkableLine
	 */
	public void onItemChecked(CheckableLine checkableLine);
	/**
	 * This is called when the l
	 * @param checkableLine
	 */
	public void onNewLineItemEdited(CheckableLine checkableLine);
}
