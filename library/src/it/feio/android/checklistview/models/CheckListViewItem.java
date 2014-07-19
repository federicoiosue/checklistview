package it.feio.android.checklistview.models;


import it.feio.android.checklistview.App;
import it.feio.android.checklistview.R;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.interfaces.EditTextEventListener;
import it.feio.android.checklistview.utils.AlphaManager;
import it.feio.android.checklistview.utils.DensityUtil;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.DragEvent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public class CheckListViewItem extends LinearLayout implements
		OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnEditorActionListener, TextWatcher, EditTextEventListener {
	
	private Context mContext;
	private ImageView dragHandler;
	private CheckBox checkBox;
	private EditTextMultiLineNoEnter editText;
	private ImageView imageView;
	private boolean showDeleteIcon;
	private CheckListEventListener mCheckListEventListener;
	private CheckListChangedListener mCheckListChangedListener;
	private int lenghtBeforeTextChanged;

	public CheckListViewItem(Context context, boolean isChecked, boolean showDeleteIcon) {
		super(context);
		this.mContext = context;
		this.showDeleteIcon = showDeleteIcon;
		
		setOrientation(HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		initDragHandler();
		initCheckBox();
		initEditText();
		addDeleteIcon();
		
		// If row was previously checked its state have to be restored
		if (isChecked) {
			checkBox.setChecked(true);
			onCheckedChanged(checkBox, true);
		}
		
		setTag(Constants.TAG_ITEM);
	}
	
	
	
	private void initDragHandler(){
		if (Build.VERSION.SDK_INT >= 11 && App.getSettings().getDragEnabled()) {
			dragHandler = new ImageView(mContext);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.TOP;
			lp.setMargins(0, DensityUtil.dpToPx(6, mContext), 0, 0);
			dragHandler.setLayoutParams(lp);
			dragHandler.setImageResource(R.drawable.ic_drag_handle);
			dragHandler.setPadding(2, 2, 2, 2);
			dragHandler.setTag(Constants.TAG_DRAG_HANDLER);
			addView(dragHandler);
		}
	}
	
	
	private void initCheckBox() {
		checkBox = new CheckBox(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.TOP;
		checkBox.setLayoutParams(lp);
		checkBox.setPadding(2, 5, 2, 2);
		checkBox.setOnCheckedChangeListener(this);
		addView(checkBox);
	}
	
	
	private void initEditText() {
		editText = new EditTextMultiLineNoEnter(mContext);
		LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		lp.gravity = Gravity.TOP;
		editText.setLayoutParams(lp);
		editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		// Alignment to support RTL
		if (Build.VERSION.SDK_INT >= 18) {
			editText.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
		}
		// Listeners
		editText.setOnFocusChangeListener(this);
		editText.setOnEditorActionListener(this);
		editText.addTextChangedListener(this);
		editText.setEditTextEventListener(this);
		editText.setTag(Constants.TAG_EDITTEXT);
		addView(editText);
	}
	
	

	public void setItemCheckedListener(CheckListEventListener listener) {
		this.mCheckListEventListener = listener;
	}
	

	@SuppressLint("NewApi") private void addDeleteIcon() {
		if (showDeleteIcon && imageView == null) {
			imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.ic_action_cancel);
			imageView.setBackgroundResource(R.drawable.icon_selector);
			int size = DensityUtil.dpToPx(30, mContext);
			LayoutParams lp = new LayoutParams(size, size);
			lp.setMargins(0, DensityUtil.dpToPx(5, mContext), 0, 0);
			imageView.setLayoutParams(lp);
			
			int padding = DensityUtil.dpToPx(2, mContext);
			imageView.setPadding(padding, padding, padding, padding);
			
			// Alpha is set just for newer API because using AlphaManager helper class I should use 
			// an animation making this way impossible to set visibility to INVISIBLE
			if (Build.VERSION.SDK_INT >= 11)
				imageView.setAlpha( 0.7f);
			imageView.setVisibility(View.INVISIBLE);
			imageView.setOnClickListener(this);
			addView(imageView);
		}
	}


	public ImageView getDragHandler() {
		return this.dragHandler;
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

	public EditTextMultiLineNoEnter getEditText() {
		return editText;
	}

	public void setEditText(EditTextMultiLineNoEnter editText) {
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

	public void setHint(Spanned text) {
		getEditText().setHint(text);
	}
	
	
	public boolean isFirstItem(){
		return (this.equals(getParentView().getChildAt(0)));
	}
	
	
	public boolean isLastItem(){
		int lastIndex = getParentView().getChildCount() - 1;
		return (this.equals(getParentView().getChildAt(lastIndex)));
	}
	

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// When a line gains focus deletion icon (if present) will be shown
		if (hasFocus) {
			if (imageView != null)
				imageView.setVisibility(View.VISIBLE);
		} else {
			// When a line loose focus checkbox will be activated
			// but only if some text has been inserted
			if (getEditText().getText().length() > 0) {
				CheckBox c = getCheckBox();
				c.setEnabled(true);
				setCheckBox(c);
			}
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
		// Item checked is notified
		if (mCheckListEventListener != null)
			mCheckListEventListener.onItemChecked(this, isChecked);
	}

	
	/**
	 * Deletion icon click
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		final ViewGroup parent = (ViewGroup) getParent();
		final View mCheckabeLine = this;
		if (parent != null) {
			// Deletion is delayed of a second
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					focusView(View.FOCUS_DOWN);
					parent.removeView(mCheckabeLine);
					mCheckListEventListener.onLineDeleted((CheckListViewItem) mCheckabeLine);
				}
			}, Constants.DELETE_ITEM_DELAY);

		}
	}
	

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		mCheckListEventListener.onEditorActionPerformed(this, actionId, event);		
		return true;
	}
	

	@Override
	public void afterTextChanged(Editable s) {}

	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		lenghtBeforeTextChanged = s.length();
	}

	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Checks if is the first text written here
		if (lenghtBeforeTextChanged == 0) {			
			// If the actual edited line is the last but one a new empty 
			// line is cremCheckableLineated at its bottom
			if (isHintItem()) {
				mCheckListEventListener.onNewLineItemEdited(this);
			}
			// Add delete icon and remove hint 
			showDeleteIcon = true;
			addDeleteIcon();
			setHint("");
		} else if (s.length() == 0) {
			if (isLastItem()) {
				// An upper line is searched to give it focus
				focusView(View.FOCUS_DOWN);					
				getParentView().removeView(this);
			}
		}		
		
		// Notify somethign is changed
		if (this.mCheckListChangedListener != null) {
			mCheckListChangedListener.onCheckListChanged();
		}
	}



	private void focusView(int focusDirection) {
		View v = focusSearch(focusDirection);
		if (v != null && v.getClass().isAssignableFrom(EditTextMultiLineNoEnter.class)) {
			EditTextMultiLineNoEnter focusableEditText = (EditTextMultiLineNoEnter) v;
			if (focusableEditText != null) {
				focusableEditText.requestFocus();
				focusableEditText.setSelection(focusableEditText.getText().length());
			}	
		}	
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText edittext)
    {
        if (edittext != null)
        {
            Drawable drawable = edittext.getBackground();
            if (android.os.Build.VERSION.SDK_INT < 16)
            {
                getEditText().setBackgroundDrawable(drawable);
            } else
            {
                getEditText().setBackground(drawable);
            }
            getEditText().setTypeface(edittext.getTypeface());
            getEditText().setTextSize(0, edittext.getTextSize());
            getEditText().setTextColor(edittext.getTextColors());
            getEditText().setLinkTextColor(edittext.getLinkTextColors());
        }
    }


	public void setCheckListChangedListener(CheckListChangedListener mCheckListChangedListener) {
		this.mCheckListChangedListener = mCheckListChangedListener;
	}
	
	
	
	/**
	 * Checks if is the hint item
	 * @return
	 */
	public boolean isHintItem() {
		boolean res = false;
		if (!getCheckBox().isEnabled()) {
			res = true;
		}
		return res;
	}


	@Override
	public void onDeletePressed() {
		// When this is catched if text is empty the current item will 
		// be removed and focus moved to item above.
		if (!isHintItem() && getText().length() == 0) {
			if (imageView != null) {
//				imageView.performClick();
				focusView(View.FOCUS_UP);
				((ViewGroup) getParent()).removeView(this);
				mCheckListEventListener.onLineDeleted(this);
			}
		}
	}
	
	
	@Override
	public void setOnDragListener(final OnDragListener l) {
		super.setOnDragListener(l);
		this.getEditText().setOnDragListener(new OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {
				switch (event.getAction()) {					
					case DragEvent.ACTION_DRAG_STARTED:
						return true;
					case DragEvent.ACTION_DRAG_LOCATION:
//						return l.onDrag(v, event);
						return false;
					case DragEvent.ACTION_DROP:
						return l.onDrag(v, event);
					default:
						return true;
				}	
//				if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
//					return false;
//				}
//				else if (event.getAction() == DragEvent.ACTION_DROP) {
//					return l.onDrag(v, event);
//				}
//				else {
//					return true;
//				}
			}
		});
//		this.getEditText().setOnDragListener(new ChecklistViewItemOnDragListener());
	}
	
	
	public CheckListView getParentView() {
		return (CheckListView)getParent();
	}
}
