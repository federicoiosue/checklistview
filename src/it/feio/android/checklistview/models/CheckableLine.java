package it.feio.android.checklistview.models;

import it.feio.android.checklistview.Constants;
import it.feio.android.checklistview.R;
import it.feio.android.checklistview.utils.AlphaManager;
import android.content.Context;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CheckableLine extends LinearLayout implements
		OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnEditorActionListener {

	private final String TAG = Constants.TAG;
	
	private CheckBox checkBox;
	private EditText editText;
	private ImageView imageView;

	public CheckableLine(Context context, boolean showDeleteIcon) {
		super(context);
		setOrientation(HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// Define CheckBox
		checkBox = new CheckBox(context);
		checkBox.setOnCheckedChangeListener(this);
		addView(checkBox);

		// Define EditText
		editText = new EditText(context);
		editText.setSingleLine(true);
		editText.setOnFocusChangeListener(this);
		editText.setOnEditorActionListener(this);
		addView(editText);

		// Define ImageView
		if (showDeleteIcon) {
			imageView = new ImageView(context);
			imageView.setImageResource(R.drawable.ic_action_cancel);
			imageView.setOnClickListener(this);
			imageView.setVisibility(View.INVISIBLE);
			addView(imageView);
		}
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

	public String getHint() {
		return getEditText().getHint().toString();
	}

	public void setHint(String text) {
		getEditText().setHint(text);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (imageView != null)
				imageView.setVisibility(View.VISIBLE);
		} else {
			if (imageView != null)
				imageView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			editText.setPaintFlags(editText.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			AlphaManager.setAlpha(editText, 0.4F);
		} else {
			editText.setPaintFlags(editText.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			AlphaManager.setAlpha(editText, 1F);
		}
	}

	@Override
	public void onClick(View v) {	
		ViewGroup parent = (ViewGroup) getParent();
		if (parent != null) {
			parent.removeView(this);
		}
	}
	

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	


}
