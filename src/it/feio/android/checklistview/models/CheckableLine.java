package it.feio.android.checklistview.models;

import com.neopixl.pixlui.components.edittext.EditText;

import it.feio.android.checklistview.R;
import it.feio.android.checklistview.utils.AlphaManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CheckableLine extends LinearLayout implements
		OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnEditorActionListener, TextWatcher {
	
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
		setGravity(Gravity.CENTER_VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// Define CheckBox
		checkBox = new CheckBox(context);
		checkBox.setOnCheckedChangeListener(this);
		addView(checkBox);

		// Define EditText
		editText = new EditText(context);
		editText.setSingleLine(true);
		LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		editText.setLayoutParams(lp);
		// Listeners
		editText.setOnFocusChangeListener(this);
		editText.setOnEditorActionListener(this);
		editText.addTextChangedListener(this);
		addView(editText);

		// Define ImageView
		addDeleteIcon();
	}

	private void addDeleteIcon() {
		if (showDeleteIcon) {
			imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.ic_action_cancel);
			imageView.setOnClickListener(this);
			imageView.setVisibility(View.INVISIBLE);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(imageView);
		}
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		for (int i = 0; i <	getChildCount(); i++){
			if (getChildAt(i).equals(this.checkBox)) {
				removeViewAt(i);
				addView(checkBox, i);
			}
		}
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
		// When a line gains focus deletion icon (if present) will be shown
		if (hasFocus) {
			if (imageView != null)
				imageView.setVisibility(View.VISIBLE);
		} else {
			// When a line loose focus checkbox will be activated
			CheckBox c = getCheckBox();
			c.setEnabled(true);
			setCheckBox(c);
			// And deletion icon (if present) will hide
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

	
	/**
	 * Deletion icon click
	 * @param v
	 */
	@Override
	public void onClick(View v) {	
		ViewGroup parent = (ViewGroup) getParent();
		if (parent != null) {
			focusView(View.FOCUS_DOWN);
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
				// If the actual edited line is the last but one a new empty 
				// line is created at its bottom
				if (this.equals(parent.getChildAt(last))) {
					CheckableLine mCheckableLine = new CheckableLine(mContext, false);
					mCheckableLine.cloneStyles(getEditText());
					mCheckableLine.setHint(getHint());
					mCheckableLine.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
					CheckBox c = mCheckableLine.getCheckBox();
					c.setEnabled(false);
					mCheckableLine.setCheckBox(c);
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
					focusView(View.FOCUS_DOWN);
					
					parent.removeView(this);
				}
			}
		}		
	}



	private void focusView(int focusDirection) {
		EditText focusableEditText = (EditText) focusSearch(focusDirection);
		if (focusableEditText != null) {
			focusableEditText.requestFocus();
			focusableEditText.setSelection(focusableEditText.getText().length());
		}		
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {
		
		// Cloning background
		Drawable b = v.getBackground();
		if (Build.VERSION.SDK_INT < 16) {
			getEditText().setBackgroundDrawable(b);
		} else {
			getEditText().setBackground(b);
		}
		
		// Cloning typography
		getEditText().setTypeface(v.getTypeface());
	}


}
