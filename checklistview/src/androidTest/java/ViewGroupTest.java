import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckListViewItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class ViewGroupTest extends InstrumentationTestCase {

	private Context context;
	private CheckListView viewGroup;
	private CheckListViewItem checkListViewItem;


	@Before
	protected void setUp() {
		context = getInstrumentation().getContext();
		viewGroup = new CheckListView(context);
		checkListViewItem = new CheckListViewItem(context, false, false);
	}


	@Test
	public void testChildAddRemoval() {
		viewGroup.addView(checkListViewItem);
		assertTrue(viewGroup.indexOfChild(checkListViewItem) >= 0);
		assertTrue(checkListViewItem.getParent().equals(viewGroup));

		viewGroup.removeView(checkListViewItem);
		assertFalse(viewGroup.indexOfChild(checkListViewItem) >= 0);
		assertFalse(viewGroup.equals(checkListViewItem.getParent()));
		assertTrue(checkListViewItem.getParent() == null);
	}

}
