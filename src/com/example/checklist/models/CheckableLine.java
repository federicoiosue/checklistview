package com.example.checklist.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CheckableLine extends LinearLayout implements OnCheckedChangeListener {

	private CheckBox checkBox;
	private EditText editText;

	public CheckableLine(Context context) {
		super(context);
		setOrientation(HORIZONTAL);
		
		checkBox = new CheckBox(context);
		editText = new EditText(context);
		
		checkBox.setOnCheckedChangeListener(this);
		
		addView(checkBox);
		addView(editText);
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public EditText getEditText() {
		return editText;
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}

	public boolean isChecked() {
		return getCheckBox().isChecked();
	}

	public String getText() {
		return getEditText().getText().toString();
	}

	public void setText(String text) {
		getEditText().setText(text);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			setAlpha(editText, 0.4F);
		} else { 
			editText.setPaintFlags(editText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
			setAlpha(editText, 1F);
		}
	}
	
		
	@SuppressLint("NewApi")
	private void setAlpha(View v, float alpha) {
		if (Build.VERSION.SDK_INT < 11) {
	        final AlphaAnimation animation = new AlphaAnimation(1F, alpha);
//	        animation.setDuration(500);
	        animation.setFillAfter(true);
	        v.startAnimation(animation);
	    } else {
	    	v.setAlpha(alpha);
		}
	}

}
