import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import it.feio.android.checklistview.dragging.ChecklistViewItemOnDragListener;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckListViewItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class ViewGroupTest extends InstrumentationTestCase {

	private Context context;
	private CheckListView checkListView;
	private CheckListViewItem checkListViewItem;


	@Before
	protected void setUp() {
		context = getInstrumentation().getContext();
		checkListView = new CheckListView(context);
		checkListViewItem = new CheckListViewItem(context, false, false);
	}


	@Test
	public void testChildAddRemoval() {
		checkListView.addView(checkListViewItem);
		assertTrue(checkListView.indexOfChild(checkListViewItem) >= 0);
		assertTrue(checkListViewItem.getParent().equals(checkListView));

		checkListView.removeView(checkListViewItem);
		assertFalse(checkListView.indexOfChild(checkListViewItem) >= 0);
		assertFalse(checkListView.equals(checkListViewItem.getParent()));
		assertTrue(checkListViewItem.getParent() == null);
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Test
	public void testChildMovementlWithLayoutTransition() {
		checkListView.setLayoutTransition(new LayoutTransition());

		CheckListViewItem checkListViewItem1 = new CheckListViewItem(context, false, false);
		checkListView.addView(checkListViewItem);
		checkListView.addView(checkListViewItem1);
		assertTrue(checkListView.indexOfChild(checkListViewItem) == 0);
		assertTrue(checkListView.indexOfChild(checkListViewItem1) == 1);
		assertTrue(checkListViewItem.getParent().equals(checkListView));
		assertTrue(checkListViewItem1.getParent().equals(checkListView));

		ChecklistViewItemOnDragListener checklistViewOnTouchListener = new ChecklistViewItemOnDragListener();
		checkListView.setOnDragListener(checklistViewOnTouchListener);
		checklistViewOnTouchListener.moveView(checkListViewItem, checkListViewItem1);
		assertTrue(checkListView.indexOfChild(checkListViewItem) == 1);
		assertTrue(checkListView.indexOfChild(checkListViewItem1) == 0);


	}

}
