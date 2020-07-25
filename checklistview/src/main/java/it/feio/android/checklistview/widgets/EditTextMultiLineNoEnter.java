package it.feio.android.checklistview.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import com.neopixl.pixlui.components.edittext.EditText;
import it.feio.android.checklistview.interfaces.EditTextEventListener;

/**
 * Class used to avoid carriage return in multi-line EditText.
 */
public class EditTextMultiLineNoEnter extends EditText {

  private EditTextEventListener mEditTextEventListener;

  public EditTextMultiLineNoEnter (Context context) {
    super(context.getApplicationContext());
  }

  public EditTextMultiLineNoEnter (Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EditTextMultiLineNoEnter (Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public InputConnection onCreateInputConnection (EditorInfo outAttrs) {
    InputConnection connection = super.onCreateInputConnection(outAttrs);
    // By default setting android:inputType="textMultiLine" will remove any imeAction like NEXT, DONE...
    // So here is where this behaviour is changed
    if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
      outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
    }
    return new DelCatcherInputConnection(connection, true);
  }


  /**
   * Sets event linstener to catch delete key pressions
   */
  public void setEditTextEventListener (EditTextEventListener mEditTextEventListener) {
    this.mEditTextEventListener = mEditTextEventListener;
  }


  /**
   * Overriding InputConnectionWrapper to throw delete key pressions
   */
  private class DelCatcherInputConnection extends InputConnectionWrapper {

    public DelCatcherInputConnection (InputConnection target, boolean mutable) {
      super(target, mutable);
    }

    @Override
    public boolean sendKeyEvent (KeyEvent event) {
      if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
        mEditTextEventListener.onDeletePressed();
      }
      return super.sendKeyEvent(event);
    }

    @Override
    public boolean deleteSurroundingText (int beforeLength, int afterLength) {
      mEditTextEventListener.onDeletePressed();
      return super.deleteSurroundingText(beforeLength, afterLength);
    }

  }

}
