import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import androidx.test.runner.AndroidJUnit4;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckListViewItem;
import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.checklistview.test.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ChecklistManagerTest {

  private Context context;
  private ChecklistManager checklistManager;
  private EditText editText;

  @Before
  public void setUp () {
    context = getInstrumentation().getContext();
    checklistManager = new ChecklistManager(context);
    editText = new EditText(context);
    editText.setText(R.string.template_phrase);
  }

  @Test
  public void convert_toList () throws ViewNotSupportedException {
    View view = checklistManager.convert(editText);

    assertEquals(CheckListView.class, view.getClass());
    assertEquals(
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        ((CheckListView) view).getChildAt(1).getText());
  }

  @Test
  public void convert_toText () throws ViewNotSupportedException {
    View view = checklistManager.convert(checklistManager.convert(editText));

    assertEquals(EditText.class, view.getClass());
    assertEquals(context.getResources().getString(R.string.template_phrase), ((EditText) view).getText().toString());
  }

  @Test
  public void getCheckedCount () throws ViewNotSupportedException {
    CheckListView checkListView = (CheckListView) checklistManager.convert(editText);
    checkItem(checkListView, 0);
    checkItem(checkListView, 2);

    assertEquals(2, checklistManager.getCheckedCount());
  }

  @Test
  public void moveCheckedToBottom () throws ViewNotSupportedException {
    CheckListView checkListView = (CheckListView) checklistManager.convert(editText);
    CheckListViewItem item = checkListView.getChildAt(0);
    checkItem(checkListView, 0);

    assertEquals(item, checkListView.getChildAt(0));

    checklistManager.moveCheckedToBottom();

    assertNotEquals(item, checkListView.getChildAt(0));
    assertEquals(item, checkListView.getChildAt(checkListView.getChildCount() - 1));
  }

  private void checkItem (CheckListView checkListView, int i) {
    CheckListViewItem item = checkListView.getChildAt(i);
    item.getCheckBox().setChecked(true);
    checkListView.onItemChecked(item, true);
  }

}
