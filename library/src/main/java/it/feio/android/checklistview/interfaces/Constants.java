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
	 * Default value for dragging feature
	 */
	public static final boolean DRAG_ENABLED = true;

	public static final String UNCHECKED_SYM = "[ ] ";
	public static final String CHECKED_SYM = "[x] ";
    public static final String UNCHECKED_ENTITY = "&EmptySmallSquare; ";
    public static final String CHECKED_ENTITY = "&#x2713; ";

	public static final int DELETE_ITEM_DELAY = 350;
	
	// Views tags
	static final String TAG_LIST = "lt";
	static final String TAG_ITEM = "it";
	static final String TAG_DRAG_HANDLER = "dh";
	static final String TAG_EDITTEXT = "et";

	// Drag and drop parameters
	public static final boolean DRAG_VIBRATION_ENABLED = false;
	public static final int DRAG_VIBRATION_DURATION = 25;
	public static final int SCROLLING_DELAY = 10;
	public static final int SCROLLING_STEP = 5;
	public static final int SCROLLING_THREESHOLD = 100;
	
	
}
