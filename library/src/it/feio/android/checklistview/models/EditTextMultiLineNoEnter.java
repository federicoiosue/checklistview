package it.feio.android.checklistview.models;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Class used to avoid carriage return in multi-line EditText.
 *
 */
public class EditTextMultiLineNoEnter extends EditText {

	public EditTextMultiLineNoEnter(Context context) {
		super(context);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection connection = super.onCreateInputConnection(outAttrs);
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
		return connection;
	}

}
