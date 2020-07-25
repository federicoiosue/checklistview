package it.feio.android.checklistview.models;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
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
import it.feio.android.checklistview.App;
import it.feio.android.checklistview.R;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.interfaces.EditTextEventListener;
import it.feio.android.checklistview.utils.AlphaManager;
import it.feio.android.checklistview.widgets.EditTextMultiLineNoEnter;
import java.lang.ref.WeakReference;


public class CheckListViewItem extends LinearLayout implements
    OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnEditorActionListener, TextWatcher,
    EditTextEventListener {


  private final WeakReference<Context> mContext;
  private ImageView dragHandler;
  private CheckBox checkBox;
  private EditTextMultiLineNoEnter editText;
  private ImageView deleteIcon;
  private boolean showDeleteIcon;
  private CheckListEventListener mCheckListEventListener;
  private CheckListChangedListener mCheckListChangedListener;
  private int lengthBeforeTextChanged;
  private boolean deletionUndone;
  private boolean undoBarEnabled = true;
  private View undoBarContainerView;


  public CheckListViewItem (WeakReference<Context> context, boolean isChecked, boolean showDeleteIcon) {
    super(context.get());
    this.mContext = context;
    this.showDeleteIcon = showDeleteIcon;
    inflate(context.get(), R.layout.checklistview_item, this);

    initDragHandler();
    initCheckBox();
    initEditText();
    initDeleteIcon();

    if (isChecked) {
      checkBox.setChecked(true);
      onCheckedChanged(checkBox, true);
    }
    setTag(Constants.TAG_ITEM);
  }


  private void initDragHandler () {
    if (Build.VERSION.SDK_INT >= 11 && App.getSettings().getDragEnabled()) {
      dragHandler = (ImageView) findViewWithTag(mContext.get().getString(R.string.tag_draghandle));
    }
  }


  private void initCheckBox () {
    checkBox = (CheckBox) findViewWithTag(mContext.get().getString(R.string.tag_checkbox));
    checkBox.setOnCheckedChangeListener(this);
  }


  private void initEditText () {
    editText = (EditTextMultiLineNoEnter) findViewWithTag(mContext.get().getString(R.string.tag_edittext));
    editText.setOnFocusChangeListener(this);
    editText.setOnEditorActionListener(this);
    editText.addTextChangedListener(this);
    editText.setEditTextEventListener(this);
  }


  void setItemCheckedListener (CheckListEventListener listener) {
    this.mCheckListEventListener = listener;
  }


  @SuppressLint("NewApi")
  private void initDeleteIcon () {
    if (showDeleteIcon && deleteIcon == null) {
      deleteIcon = (ImageView) findViewWithTag(mContext.get().getString(R.string.tag_deleteicon));
      // Alpha is set just for newer API because using AlphaManager helper class I should use
      // an animation making this way impossible to set visibility to INVISIBLE
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        deleteIcon.setAlpha(0.7f);
      }
      deleteIcon.setOnClickListener(this);
    }
  }


  ImageView getDragHandler () {
    return this.dragHandler;
  }


  CheckBox getCheckBox () {
    return checkBox;
  }


  void setCheckBox (CheckBox checkBox) {
    for (int i = 0; i < getChildCount(); i++) {
      if (getChildAt(i).equals(this.checkBox)) {
        removeViewAt(i);
        addView(checkBox, i);
      }
    }
    this.checkBox = checkBox;
  }


  public EditTextMultiLineNoEnter getEditText () {
    return editText;
  }


  public void setEditText (EditTextMultiLineNoEnter editText) {
    this.editText = editText;
  }


  public boolean isChecked () {
    return getCheckBox().isChecked();
  }


  public String getText () {
    return getEditText().getText().toString();
  }


  public void setText (String text) {
    getEditText().setText(text);
  }


  public String getHint () {
    return getEditText().getHint() != null ? getEditText().getHint().toString() : "";
  }


  public void setHint (String text) {
    getEditText().setHint(text);
  }


  public void setHint (Spanned text) {
    getEditText().setHint(text);
  }


  public boolean isFirstItem () {
    return equals(getParentView().getChildAt(0));
  }


  public boolean isLastItem () {
    return equals(getParentView().getChildAt(getParentView().getChildCount() - 1));
  }


  @Override
  public boolean equals (Object o) {
    return super.equals(o);
  }


  @Override
  public int hashCode () {
    return super.hashCode();
  }


  @Override
  public void onFocusChange (View v, boolean hasFocus) {
    // When a line gains focus deletion icon (if present) will be shown
    if (hasFocus && deleteIcon != null) {
      deleteIcon.setVisibility(View.VISIBLE);
    } else {
      // When a line loose focus checkbox will be activated
      // but only if some text has been inserted
      if (getEditText().getText().length() > 0) {
        CheckBox c = getCheckBox();
        c.setEnabled(true);
        setCheckBox(c);
      }
      // And deletion icon (if present) will hide
      if (deleteIcon != null) {
        deleteIcon.setVisibility(View.INVISIBLE);
      }
    }
  }


  @Override
  public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      editText.setPaintFlags(editText.getPaintFlags()
          | Paint.STRIKE_THRU_TEXT_FLAG);
      AlphaManager.setAlpha(editText, 0.4F);
    } else {
      editText.setPaintFlags(editText.getPaintFlags()
          & (~Paint.STRIKE_THRU_TEXT_FLAG));
      AlphaManager.setAlpha(editText, 1F);
    }
    if (mCheckListEventListener != null) {
      mCheckListEventListener.onItemChecked(this, isChecked);
    }
  }


  /**
   * Deletion icon click
   */
  @Override
  public void onClick (View v) {
    final ViewGroup parent = (ViewGroup) getParent();
    final View mCheckableLine = this;
    if (parent != null) {
      focusView(View.FOCUS_DOWN);
      final int index = parent.indexOfChild(mCheckableLine);
      parent.removeView(mCheckableLine);
      if (undoBarEnabled) {
        showUndoBar(parent, mCheckableLine, index);
      }
    }
  }


  private void showUndoBar (final ViewGroup parent, final View mCheckableLine, final int index) {
    View snackBarContainer = undoBarContainerView != null ? undoBarContainerView : parent.getRootView()
                                                                                         .findViewById(
                                                                                             android.R.id.content);
    Snackbar.make(snackBarContainer, R.string.item_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo, new OnClickListener() {
              @Override
              public void onClick (View v) {
                parent.addView(mCheckableLine, index);
                deletionUndone = true;
              }
            })
            .setCallback(new Snackbar.Callback() {
              @Override
              public void onDismissed (Snackbar snackbar, int event) {
                if (!deletionUndone) {
                  mCheckListEventListener.onLineDeleted((CheckListViewItem) mCheckableLine);
                }
              }
            }).show();
  }


  @Override
  public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
    mCheckListEventListener.onEditorActionPerformed(this, actionId, event);
    return true;
  }


  @Override
  public void afterTextChanged (Editable s) {
    // Nothing to do
  }


  @Override
  public void beforeTextChanged (CharSequence s, int start, int count, int after) {
    lengthBeforeTextChanged = s.length();
  }


  @Override
  public void onTextChanged (CharSequence s, int start, int before, int count) {
    // Checks if is the first text written here
    if (lengthBeforeTextChanged == 0) {
      // If the actual edited line is the last but one a new empty
      // line is cremCheckableLineated at its bottom
      if (isHintItem()) {
        mCheckListEventListener.onNewLineItemEdited(this);
      }
      // Adds delete icon and remove hint
      showDeleteIcon = true;
      initDeleteIcon();
      setHint("");
    }

    if (this.mCheckListChangedListener != null) {
      mCheckListChangedListener.onCheckListChanged();
    }
  }


  private void focusView (int focusDirection) {
    View v = focusSearch(focusDirection);
    if (v != null && v.getClass().isAssignableFrom(EditTextMultiLineNoEnter.class)) {
      try {
        EditTextMultiLineNoEnter focusableEditText = (EditTextMultiLineNoEnter) v;
        focusableEditText.requestFocus();
        focusableEditText.setSelection(focusableEditText.getText().length());
      } catch (ClassCastException e) {
        Log.e(Constants.TAG, "Cast exception on focus", e);
      }
    }
  }


  @SuppressLint("NewApi")
  @SuppressWarnings("deprecation")
  public void cloneStyles (EditText edittext) {
    if (edittext != null) {
      Drawable drawable = edittext.getBackground();
      if (android.os.Build.VERSION.SDK_INT < 16) {
        getEditText().setBackgroundDrawable(drawable);
      } else {
        getEditText().setBackground(drawable);
      }
      getEditText().setTypeface(edittext.getTypeface());
      getEditText().setTextSize(0, edittext.getTextSize());
      getEditText().setTextColor(edittext.getTextColors());
      getEditText().setLinkTextColor(edittext.getLinkTextColors());
    }
  }


  void setCheckListChangedListener (CheckListChangedListener mCheckListChangedListener) {
    this.mCheckListChangedListener = mCheckListChangedListener;
  }


  void setUndoBarEnabled (boolean undoBarEnabled) {
    this.undoBarEnabled = undoBarEnabled;
  }


  /**
   * Used to set a custom View to contain item undo deletion SnackBar
   *
   * @param undoBarContainerView Container view
   */
  void setUndoBarContainerView (final View undoBarContainerView) {
    this.undoBarContainerView = undoBarContainerView;
  }


  /**
   * Checks if is the hint item
   */
  public boolean isHintItem () {
    return !getCheckBox().isEnabled();
  }


  @Override
  public void onDeletePressed () {
    // When this is catched if text is empty the current item will
    // be removed and focus moved to item above.
    if (!isHintItem() && getText().length() == 0 && deleteIcon != null) {
      focusView(View.FOCUS_UP);
      ((ViewGroup) getParent()).removeView(this);
      mCheckListEventListener.onLineDeleted(this);
    }
  }


  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public void setOnDragListener (final OnDragListener l) {
    super.setOnDragListener(l);
    this.getEditText().setOnDragListener(new OnDragListener() {
      @Override
      public boolean onDrag (View v, DragEvent event) {
        return manageDragEvents(v, event, l);
      }
    });
  }


  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private boolean manageDragEvents (View v, DragEvent event, OnDragListener l) {
    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        return l.onDrag(v, event);
      case DragEvent.ACTION_DRAG_LOCATION:
        return false;
      case DragEvent.ACTION_DROP:
        return l.onDrag(v, event);
      default:
        return true;
    }
  }


  public CheckListView getParentView () {
    return (CheckListView) getParent();
  }
}
