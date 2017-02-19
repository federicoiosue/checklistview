package it.feio.android.checklistview.dragging;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import it.feio.android.checklistview.App;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewOnTouchListener implements OnTouchListener {

	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
			actionDown(view);
			return true;
		}
		return false;
	}


    private void actionDown(View view) {
        View v = (View) view.getParent();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			v.startDrag(null, new ChecklistViewDragShadowBuilder(v), v, 0);
		} else {
			v.startDragAndDrop(null, new ChecklistViewDragShadowBuilder(v), v, 0);
		}
        if (App.getSettings().getDragVibrationEnabled()) {
            ((Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(App.getSettings()
                    .getDragVibrationDuration());
        }
    }
}
