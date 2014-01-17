package it.feio.android.checklistview.models;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CheckListView extends LinearLayout {

	ArrayList<CheckableLine> lines = new ArrayList<CheckableLine>();

	public CheckListView(Context context) {
		super(context);
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {
		
//		Drawable d = v.getBackground();
//		if (Build.VERSION.SDK_INT < 16) {
//			setBackgroundDrawable(d);
//		} else {
//			setBackground(d);
//		}
		for (int i = 0; i < getChildCount(); i++) {
			((CheckableLine)getChildAt(i)).cloneStyles(v);
		}
	}
	
	
	/**
	 * Retrieve the edittext of a child line to be used to copy the typography
	 * @return
	 */
	public EditText getEditText() {
		EditText res = null;
		CheckableLine child = (CheckableLine)getChildAt(0);
		if (child != null)
			res = child.getEditText();
		return res;
	}
	

}
