package it.feio.android.checklistview.dragging;

import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.models.CheckListViewItem;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewOnDragListener implements OnDragListener {



	public boolean onDrag(View target, DragEvent event) {
		int action = event.getAction();
		final View dragged = (View) event.getLocalState();
		switch (action) {
			case DragEvent.ACTION_DRAG_STARTED:
				Log.d(Constants.TAG, "Drag event started");
				dragged.setVisibility(View.INVISIBLE);
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d(Constants.TAG, "Drag event entered into " + target.toString());
				if (target.getClass().isAssignableFrom(CheckListViewItem.class)) {
					dragged.setVisibility(View.INVISIBLE);
					ViewGroup container = (ViewGroup) dragged.getParent();
					int index = container.indexOfChild(target);
					container.removeView(dragged);
					container.addView(dragged, index);
				}
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.d(Constants.TAG, "Drag event exited from " + target.toString());
				if (target.equals(dragged.getParent())) {
					showViewWithDelay(dragged);
				}
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
//				x = event.getX();
//				y = event.getY();
//				Log.v(Constants.TAG, "Drag event position " + x + ", " + y);
				break;
			case DragEvent.ACTION_DROP:
				Log.d(Constants.TAG, "Dropped into " + target.toString());
				showViewWithDelay(dragged);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				Log.d(Constants.TAG, "Drag ended");				
				break;
			default:
				break;
		}
		return true;
	}
	
	private void showViewWithDelay(final View v) {
		v.post(new Runnable() {
			@Override
			public void run() {
				v.setVisibility(View.VISIBLE);
			}
		});
	}
}
