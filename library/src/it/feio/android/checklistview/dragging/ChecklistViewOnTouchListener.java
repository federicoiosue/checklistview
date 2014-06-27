package it.feio.android.checklistview.dragging;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewOnTouchListener implements OnTouchListener {

	public boolean onTouch(View view, MotionEvent motionEvent) {
		int action = motionEvent.getAction();

		switch (action) {

			case MotionEvent.ACTION_DOWN:
				View v = (View) view.getParent();
				ChecklistViewDragShadowBuilder shadowBuilder = new ChecklistViewDragShadowBuilder(v);
				v.startDrag(null, shadowBuilder, v, 0);
				return true;

			default:
				return false;
		}
	}
}