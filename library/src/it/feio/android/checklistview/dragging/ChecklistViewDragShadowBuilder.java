package it.feio.android.checklistview.dragging;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.View.DragShadowBuilder;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChecklistViewDragShadowBuilder extends DragShadowBuilder {

//	private static Drawable shadow;


	ChecklistViewDragShadowBuilder(View view) {
		super(view);
		// shadow = new ColorDrawable(0xffcccccc);
	}
    

	public void onProvideShadowMetrics(Point size, Point touch) {
		int width = getView().getWidth();
		int height = getView().getHeight();
//		shadow.setBounds(0, 0, width, height);
		size.set(width, height);
		touch.set(10, 10);
	}


//	public void onDrawShadow(Canvas canvas) {
//		shadow.draw(canvas);
//	}
}
