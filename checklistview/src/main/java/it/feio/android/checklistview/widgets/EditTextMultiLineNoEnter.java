package it.feio.android.checklistview.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.neopixl.pixlui.components.edittext.EditText;
import it.feio.android.checklistview.widgets.EditableAccomodatingLatinIMETypeNullIssues;
import it.feio.android.checklistview.widgets.InputConnectionAccomodatingLatinIMETypeNullIssues;
import it.feio.android.checklistview.interfaces.EditTextEventListener;


/**
 * Class used to avoid carriage return in multi-line EditText.
 */
public class EditTextMultiLineNoEnter extends EditText {

	private EditTextEventListener mEditTextEventListener;


	public EditTextMultiLineNoEnter(Context context) {
		super(context.getApplicationContext());
	}


	public EditTextMultiLineNoEnter(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public EditTextMultiLineNoEnter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		//Passing FALSE as the SECOND ARGUMENT (fullEditor) to the constructor
		// will result in the key events continuing to be passed in to this
		// view.  Use our special BaseInputConnection-derived view
		InputConnectionAccomodatingLatinIMETypeNullIssues baseInputConnection =
				new InputConnectionAccomodatingLatinIMETypeNullIssues(this, false);

		//In some cases an IME may be able to display an arbitrary label for a
		// command the user can perform, which you can specify here.  A null value
		// here asks for the default for this key, which is usually something
		// like Done.
//		outAttrs.actionLabel = null;

		//Special content type for when no explicit type has been specified.
		// This should be interpreted (by the IME that invoked
		// onCreateInputConnection())to mean that the target InputConnection
		// is not rich, it can not process and show things like candidate text
		// nor retrieve the current text, so the input method will need to run
		// in a limited "generate key events" mode.  This disables the more
		// sophisticated kinds of editing that use a text buffer.
//		outAttrs.inputType = InputType.TYPE_NULL;

		outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT;

		return baseInputConnection;
	}


	/**
	 * Sets event linstener to catch delete key pressions
	 */
	public void setEditTextEventListener(EditTextEventListener mEditTextEventListener) {
		this.mEditTextEventListener = mEditTextEventListener;
		setOnKeyListener(onKeyListener);
	}


	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() != KeyEvent.ACTION_DOWN) {
				//We only look at ACTION_DOWN in this code, assuming that ACTION_UP is redundant.
				// If not, adjust accordingly.
				return false;
			} else if (event.getUnicodeChar() ==
					(int) EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER.charAt(0)) {
				//We are ignoring this character, and we want everyone else to ignore it, too, so
				// we return true indicating that we have handled it (by ignoring it).
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_DEL) {
				mEditTextEventListener.onDeletePressed();
				return false;
			}
			return false;
		}
	};

}


