package it.feio.android.checklistview.models;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class CheckListView extends LinearLayout {

	ArrayList<CheckableLine> lines = new ArrayList<CheckableLine>();

	public CheckListView(Context context) {
		super(context);
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneBackground(Drawable d) {

		if (Build.VERSION.SDK_INT < 16) {
			setBackgroundDrawable(d);
		} else {
			setBackground(d);
		}
		for (int i = 0; i < getChildCount(); i++) {
			if (Build.VERSION.SDK_INT < 16) {
				((CheckableLine)getChildAt(i)).getEditText().setBackgroundDrawable(d);
			} else {
				((CheckableLine)getChildAt(i)).getEditText().setBackground(d);
			}			
		}
	}
	

}
