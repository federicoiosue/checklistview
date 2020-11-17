package it.feio.android.checklistview.dragging;

import android.graphics.Point;
import android.view.View;
import android.view.View.DragShadowBuilder;

class ChecklistViewDragShadowBuilder extends DragShadowBuilder {


  ChecklistViewDragShadowBuilder (View view) {
    super(view);
  }


  public void onProvideShadowMetrics (Point size, Point touch) {
    int width = getView().getWidth();
    int height = getView().getHeight();
    size.set(width, height);
    touch.set(10, 20);
  }

}
