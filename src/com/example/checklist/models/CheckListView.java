package com.example.checklist.models;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

public class CheckListView extends LinearLayout {

	ArrayList<CheckableLine> lines = new ArrayList<CheckableLine>();

	public CheckListView(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	public void cloneBackground(Drawable d) {
		setBackgroundDrawable(d);
		for (int i = 0; i < getChildCount(); i++) {
			((CheckableLine)getChildAt(i)).getEditText().setBackgroundDrawable(d);
		}
	}
	

}
