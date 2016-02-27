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
		return motionEvent.getAction() == MotionEvent.ACTION_DOWN ? actionDown(view) : false;

	}


    private boolean actionDown(View view) {
        View v = (View) view.getParent();
        v.startDrag(null, new ChecklistViewDragShadowBuilder(v), v, 0);
        if (App.getSettings().getDragVibrationEnabled()) {
            ((Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(App.getSettings()
                    .getDragVibrationDuration());
        }
        return true;
    }
}
