package it.feio.android.checklistview.models;

import it.feio.android.checklistview.Constants;
import it.feio.android.checklistview.R;
import it.feio.android.checklistview.utils.AlphaManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CheckableLine extends LinearLayout implements
		OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnEditorActionListener, TextWatcher {

	private final String TAG = Constants.TAG;
	
	private Context mContext;
	private CheckBox checkBox;
	private EditText editText;
	private ImageView imageView;
	private boolean showDeleteIcon;

	public CheckableLine(Context context, boolean showDeleteIcon) {
		super(context);
		this.mContext = context;
		this.showDeleteIcon = showDeleteIcon;
		
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
		editText.addTextChangedListener(this);
		addView(editText);

		// Define ImageView
		if (showDeleteIcon) {
			addDeleteIcon();
		}
	}

	private void addDeleteIcon() {
		imageView = new ImageView(mContext);
		imageView.setImageResource(R.drawable.ic_action_cancel);
		imageView.setOnClickListener(this);
		imageView.setVisibility(View.INVISIBLE);
		addView(imageView);
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
		if (getEditText().getHint() != null)
			return getEditText().getHint().toString();
		else 
			return ""; 
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

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Checks if is the first text written here
		if (before == 0 && s.length() == 1) {			
			ViewGroup parent = (ViewGroup) getParent();
			int last = parent.getChildCount() - 1;
			if (parent != null) {
				if (this.equals(parent.getChildAt(last))) {
					CheckableLine mCheckableLine = new CheckableLine(mContext, false);
					mCheckableLine.cloneBackground(getBackground());
					mCheckableLine.setHint(getHint());
					mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
					parent.addView(mCheckableLine);
				}
				// Add delete icon and remove hint 
				addDeleteIcon();
				setHint("");
			}
		} else if (s.length() == 0) {
			ViewGroup parent = (ViewGroup) getParent();
			if (parent != null) {
				int last = parent.getChildCount() - 1;
				if (this.equals(parent.getChildAt(last - 1))) {
					// An upper line is searched to give it focus
					EditText focusableEditText = (EditText) focusSearch(View.FOCUS_DOWN);
					if (focusableEditText != null) {
						focusableEditText.requestFocus();
						focusableEditText.setSelection(focusableEditText.getText().length());
					}
					
					parent.removeView(this);
				}
			}
		}		
	}



	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneBackground(Drawable d) {
		if (Build.VERSION.SDK_INT < 16) {
			getEditText().setBackgroundDrawable(d);
		} else {
			getEditText().setBackground(d);
		}
	}


}
