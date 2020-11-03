import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.test.runner.AndroidJUnit4;
import it.feio.android.checklistview.dragging.ChecklistViewItemOnDragListener;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckListViewItem;
import java.lang.ref.WeakReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ViewGroupTest {

  private WeakReference<Context> context;
  private CheckListView checkListView;
  private CheckListViewItem checkListViewItem;


  @Before
  public void setUp () {
    context = new WeakReference<>(getInstrumentation().getContext());
    checkListView = new CheckListView(context);
    checkListViewItem = new CheckListViewItem(context, false, false);
  }

  @Test
  public void testChildAddRemoval () {
    checkListView.addView(checkListViewItem);
    assertTrue(checkListView.indexOfChild(checkListViewItem) >= 0);
    assertEquals(checkListViewItem.getParent(), checkListView);

    checkListView.removeView(checkListViewItem);
    assertFalse(checkListView.indexOfChild(checkListViewItem) >= 0);
    assertNotEquals(checkListView, checkListViewItem.getParent());
    assertNull(checkListViewItem.getParent());
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Test
  public void testChildMovementlWithLayoutTransition () {
    checkListView.setLayoutTransition(new LayoutTransition());

    CheckListViewItem checkListViewItem1 = new CheckListViewItem(context, false, false);
    checkListView.addView(checkListViewItem);
    checkListView.addView(checkListViewItem1);
    assertEquals(0, checkListView.indexOfChild(checkListViewItem));
    assertEquals(1, checkListView.indexOfChild(checkListViewItem1));
    assertEquals(checkListViewItem.getParent(), checkListView);
    assertEquals(checkListViewItem1.getParent(), checkListView);

    ChecklistViewItemOnDragListener checklistViewOnTouchListener = new ChecklistViewItemOnDragListener();
    checkListView.setOnDragListener(checklistViewOnTouchListener);
    checklistViewOnTouchListener.moveView(checkListViewItem, checkListViewItem1);
    assertEquals(1, checkListView.indexOfChild(checkListViewItem));
    assertEquals(0, checkListView.indexOfChild(checkListViewItem1));
  }

}
