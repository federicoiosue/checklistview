package it.feio.android.checklistview.dragging;

import it.feio.android.checklistview.App;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.models.CheckListViewItem;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewItemOnDragListener implements OnDragListener {

	private final int SCROLLING_MARGIN = 200;

	private float y;


	public boolean onDrag(View target, DragEvent event) {
		int action = event.getAction();
		final View dragged = (View) event.getLocalState();

		switch (action) {

			case DragEvent.ACTION_DRAG_STARTED:
				Log.d(Constants.TAG, "Drag event started");
				dragged.setVisibility(View.INVISIBLE);
				y = event.getY();
				break;

			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d(Constants.TAG, "Drag event entered into " + target.toString());
				if (targetCanAcceptDrop(dragged, target)) {
					Log.d(Constants.TAG, "Entrance accepted");
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
				// Control demanded to the container to scroll
				if (target.getClass().isAssignableFrom(CheckListViewItem.class)) {
					return false;
//				} else if (target.getClass().isAssignableFrom(CheckListView.class)) {
				} else {
//					// boolean movedUp = event.getY() < y ? true : false;
//					y = event.getY();
//					Log.v(Constants.TAG, "Drag event vertical position: " + y);
//					if (y - getScrollableAncestor(dragged).getScrollY() < SCROLLING_MARGIN) {
////						int scroll = (int) (y - getScrollableAncestor(dragged).getScrollY()) * dragged.getHeight();
//						int scroll = dragged.getHeight();
//						// int scroll = movedUp ? SCROLLING_MARGIN : -SCROLLING_MARGIN;
//						getScrollableAncestor(dragged).scrollBy(0, scroll);
//					}
					
					
					ScrollView mainScrollView = (ScrollView) getScrollableAncestor(dragged);

		            int topOfDropZone = target.getTop();
		            int bottomOfDropZone = target.getBottom();

		            int scrollY = mainScrollView.getScrollY();
		            int scrollViewHeight = mainScrollView.getMeasuredHeight();

		            Log.d(Constants.TAG,"location: Scroll Y: "+ scrollY + " Scroll Y+Height: "+(scrollY + scrollViewHeight));
		            Log.d(Constants.TAG," top: "+ topOfDropZone +" bottom: "+bottomOfDropZone);

		            if (bottomOfDropZone > (scrollY + scrollViewHeight - SCROLLING_MARGIN))
		                mainScrollView.smoothScrollBy(0, 30);

		            if (topOfDropZone < (scrollY + SCROLLING_MARGIN))
		                mainScrollView.smoothScrollBy(0, -30);
					
					
					return true;
				}

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


	private View getScrollableAncestor(View dragged) {
		boolean found = false;
		View parent = (View) dragged.getParent();
		while (!found) {
			if (parent.getClass().isAssignableFrom(ScrollView.class)) {
				return parent;
			} else {
				parent = (View) parent.getParent();
			}
		}
		return null;
	}


	private boolean targetCanAcceptDrop(View dragged, View target) {
		boolean canAcceptDrop = false;
		if (dragged.getClass().isAssignableFrom(CheckListViewItem.class)
				&& target.getClass().isAssignableFrom(CheckListViewItem.class)) {
			CheckListViewItem draggedItem = (CheckListViewItem) dragged;
			CheckListViewItem targetItem = (CheckListViewItem) target;
			if (App.getSettings().getMoveCheckedOnBottom() == Settings.CHECKED_HOLD || !(draggedItem.isChecked() ^ targetItem.isChecked())) {
				canAcceptDrop = true;
			}
		}
		return canAcceptDrop;
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
