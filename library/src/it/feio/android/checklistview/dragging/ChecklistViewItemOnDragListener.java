package it.feio.android.checklistview.dragging;

import it.feio.android.checklistview.App;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.models.CheckListViewItem;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewItemOnDragListener implements OnDragListener {

	private final int SCROLLING_DELAY = 10;
	private final int SCROLLING_STEP = 3;
	private final int DIRECTION_UP = 0;
	private final int DIRECTION_DOWN = 1;

	private int dragDirection;
	private float y;
	private Thread scrollerThread;
	private boolean scroll = false;
	private ScrollView scrollView;


	public boolean onDrag(View target, DragEvent event) {
		int action = event.getAction();
		final View dragged = (View) event.getLocalState();
		scrollView = (ScrollView) getScrollableAncestor(dragged);

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
					stopScrolling();
					dragged.setVisibility(View.INVISIBLE);
					ViewGroup container = (ViewGroup) dragged.getParent();
					int index = container.indexOfChild(target);
					container.removeView(dragged);
					container.addView(dragged, index);
				}
				break;

			case DragEvent.ACTION_DRAG_EXITED:
				Log.d(Constants.TAG, "Drag event exited from " + target.toString());
				if (checkTag(target, Constants.TAG_LIST)) {
					stopScrolling();
				}
				if (target.equals(dragged.getParent())) {
					showViewWithDelay(dragged);
				}
				break;

			case DragEvent.ACTION_DRAG_LOCATION:
				// Control demanded to the container to scroll
				if (checkTag(target, Constants.TAG_LIST)) {
					y = event.getY();
					Log.v(Constants.TAG, "Drag event vertical position: " + y);
					
					Rect scrollBounds = new Rect();
					scrollView.getLocalVisibleRect(scrollBounds);
					Rect scrollBounds1 = new Rect();
					scrollView.getHitRect(scrollBounds1);
					Rect scrollBounds2 = new Rect();
					scrollView.getDrawingRect(scrollBounds2);
					
					if (y - scrollView.getScrollY() < 200) {
						dragDirection = DIRECTION_UP;
						startScrolling(target);
					} else if (scrollView.getHeight() - (y - scrollView.getScrollY()) < 200) {
						dragDirection = DIRECTION_DOWN;
						startScrolling(target);
					}
					break;
				} else {
					return false;
				}

			case DragEvent.ACTION_DROP:
				Log.d(Constants.TAG, "Dropped into " + target.toString());
				stopScrolling();
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


	private void startScrolling(View target) {
		if (!scroll) {
			scrollerThread = new Thread(new Scroller(target));
			scroll = true;
			scrollerThread.start();
		}
	}


	private void stopScrolling() {
		if (scroll) {
			scroll = false;
			if (scrollerThread != null && scrollerThread.isAlive()) {
				scrollerThread.interrupt();
			}
		}
	}
	
	
	class Scroller implements Runnable {
		View target;
		public Scroller(View target) {
			this.target = target;
		}

		@Override
		public void run() {
			int scrollStep = dragDirection == DIRECTION_UP ? -SCROLLING_STEP : SCROLLING_STEP;
			while (scroll) {
				scrollView.smoothScrollBy(0, scrollStep);
				try {
					Thread.sleep(SCROLLING_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
		if (checkTag(target, Constants.TAG_ITEM)) {
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
	
	
	private boolean checkTag(View view, Object tag) {
		if (view.getTag() != null && view.getTag().equals(tag)) {
			return true;
		} else {
			return false;
		}
	}
}
