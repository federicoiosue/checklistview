package it.feio.android.checklistview.interfaces;

public interface Constants {
	public static final String TAG = "CheckListView";

	/**
	 * Default line separator to parse simple text into checklist items
	 */
	public static final String LINES_SEPARATOR = System.getProperty("line.separator");
	/**
	 * Show or not delete icon on items
	 */
	public static final boolean SHOW_DELETE_ICON = true;
	/**
	 * Show or not an empty item on bottom of the list
	 */
	public static final boolean SHOW_HINT_ITEM = false;
	/**
	 * Keep cheched items when converting back to simple text. Otherwise they
	 * will be removed.
	 */
	public static final boolean KEEP_CHECKED = true;
	/**
	 * Shows or not checks when converting back to simple text
	 */
	public static final boolean SHOW_CHECKS = false;

	/**
	 * Checked items behavior: hold on place.
	 */
	public static final int CHECKED_HOLD = 0;
	/**
	 * Checked items behavior: move on bottom of list.
	 */
	public static final int CHECKED_ON_BOTTOM = 1;
	/**
	 * Checked items behavior: move on bottom of unchecked but on top of
	 * checked.
	 */
	public static final int CHECKED_ON_TOP_OF_CHECKED = 2;

	public static final String UNCHECKED_SYM = "[ ] ";
	public static final String CHECKED_SYM = "[x] ";

	public static final int DELETE_ITEM_DELAY = 350;
	
}
