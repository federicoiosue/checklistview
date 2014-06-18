package it.feio.android.checklistview;

import it.feio.android.checklistview.interfaces.Constants;

public class Settings {

	/**
	 * Checked items behavior: hold on place.
	 */
	public static final int CHECKED_HOLD = 0;
	/**
	 * Checked items behavior: move on bottom of list.
	 */
	public static final int CHECKED_ON_BOTTOM = 1;
	/**
	 * Checked items behavior: move on bottom of unchecked but on top of checked.
	 */
	public static final int CHECKED_ON_TOP_OF_CHECKED = 2;

	private String linesSeparator = Constants.LINES_SEPARATOR;
	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private boolean showChecks = Constants.SHOW_CHECKS;
	private boolean showHintItem = Constants.SHOW_HINT_ITEM;
	private String newEntryHint = "";
	private int moveCheckedOnBottom = CHECKED_HOLD;
	private boolean dragEnabled = Constants.DRAG_ENABLED;


	public String getLinesSeparator() {
		return linesSeparator;
	}


	public void setLinesSeparator(String linesSeparator) {
		this.linesSeparator = linesSeparator;
	}


	public boolean getShowDeleteIcon() {
		return showDeleteIcon;
	}


	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}


	public boolean getKeepChecked() {
		return keepChecked;
	}


	public void setKeepChecked(boolean keepChecked) {
		this.keepChecked = keepChecked;
	}


	public boolean getShowChecks() {
		return showChecks;
	}


	public void setShowChecks(boolean showChecks) {
		this.showChecks = showChecks;
	}


	public boolean getShowHintItem() {
		return showHintItem;
	}


	public void setShowHintItem(boolean showHintItem) {
		this.showHintItem = showHintItem;
	}


	public String getNewEntryHint() {
		return newEntryHint;
	}


	public void setNewEntryHint(String newEntryHint) {
		this.newEntryHint = newEntryHint;
	}


	public int getMoveCheckedOnBottom() {
		return moveCheckedOnBottom;
	}


	public void setMoveCheckedOnBottom(int moveCheckedOnBottom) {
		this.moveCheckedOnBottom = moveCheckedOnBottom;
	}


	public boolean getDragEnabled() {
		return dragEnabled;
	}


	public void setDragEnabled(boolean dragEnabled) {
		this.dragEnabled = dragEnabled;
	}

}
