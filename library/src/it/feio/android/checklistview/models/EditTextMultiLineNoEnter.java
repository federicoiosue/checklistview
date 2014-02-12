package it.feio.android.checklistview.models;

import it.feio.android.checklistview.interfaces.EditTextEventListener;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Class used to avoid carriage return in multi-line EditText.
 *
 */
public class EditTextMultiLineNoEnter extends EditText {
	
	private EditTextEventListener mEditTextEventListener;

	public EditTextMultiLineNoEnter(Context context) {
		super(context);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//		InputConnection connection = super.onCreateInputConnection(outAttrs);
		// Removed redundant imeAction check
//		int imeActions = outAttrs.imeOptions & EditorInfo.IME_MASK_ACTION;
//		if ((imeActions & EditorInfo.IME_ACTION_NEXT) != 0) {
//			// clear the existing action
//			outAttrs.imeOptions ^= imeActions;
//			// set the DONE action
//			outAttrs.imeOptions |= EditorInfo.IME_ACTION_NEXT;
//		}
		// By default setting android:inputType="textMultiLine" will remove any
		// imeAction like NEXT, DONE...
		// So here is where this behaviour is changed
		if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
			outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
		}
		return new DelCatcherInputConnection(super.onCreateInputConnection(outAttrs),
                true);
	}
	

	/**
	 * Sets event linstener to catch delete key pressions
	 * @param mEditTextEventListener
	 */
	public void setEditTextEventListener(EditTextEventListener mEditTextEventListener) {
		this.mEditTextEventListener = mEditTextEventListener;
	}


	/**
	 * Overriding InputConnectionWrapper to throw delete key pressions
	 *
	 */
	private class DelCatcherInputConnection extends InputConnectionWrapper {

        public DelCatcherInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//            	onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            	mEditTextEventListener.onDeletePressed();
            }
            return super.sendKeyEvent(event);
        }
        
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
//        	onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        	mEditTextEventListener.onDeletePressed();
        	return super.deleteSurroundingText(beforeLength, afterLength);
        }

    }

}
