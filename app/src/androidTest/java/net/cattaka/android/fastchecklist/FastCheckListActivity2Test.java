
package net.cattaka.android.fastchecklist;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import net.cattaka.android.fastchecklist.test.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class FastCheckListActivity2Test extends BaseTestCase<FastCheckListActivity> {
    public FastCheckListActivity2Test() {
        super(FastCheckListActivity.class);
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity() {
        FastCheckListActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by back key
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test screen transition to the edit screen.
     */
    public void testScreenTransition() throws Throwable{
        {   // Creating dummy data.
            clearData();
            createTestData(3, 4);
        }
        FastCheckListActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        final ListView listView = (ListView) activity.findViewById(R.id.list_entries);
        {   // Test number of entries
            assertEquals(3,listView.getCount());
        }
        {   // Test screen transition. on press R.id.button_check.
            Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor(CheckListCheckActivity.class.getName(), null, true);
            getInstrumentation().addMonitor(monitor);
            try {
                setSelection(listView, 0);
                CheckListEntry entry = ((CheckListEntry)listView.getItemAtPosition(0));
                onView(allOf(withId(R.id.button_check), hasSibling(withText(entry.getTitle()))))
                        .perform(click());
                assertEquals(1, monitor.getHits());
            } finally {
                 getInstrumentation().removeMonitor(monitor);
            }
        }
        {   // Test finish by back key
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test switch to edit mode and back to normal mode.
     * This checks visibilities of views.
     */
    public void testSwitchEditMode() throws Throwable{
        {   // Creating dummy data.
            clearData();
            createTestData(3, 4);
        }
        final FastCheckListActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        final ListView listView = (ListView) activity.findViewById(R.id.list_entries);
        {   // Test number of entries
            assertEquals(3, listView.getCount());
        }
        {
            {   // Check visiblity
                assertEquals(View.VISIBLE, activity.findViewById(R.id.button_enter_edit_mode).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.button_exit_edit_mode).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_up).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_down).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_edit).getVisibility());
                assertEquals(View.VISIBLE, listView.getChildAt(0).findViewById(R.id.button_check).getVisibility());
            }
            {
                onView(withId(R.id.button_enter_edit_mode))
                        .perform(click());
            }
            {   // Check visiblity
                assertEquals(View.GONE, activity.findViewById(R.id.button_enter_edit_mode).getVisibility());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.button_exit_edit_mode).getVisibility());
                assertEquals(View.VISIBLE, listView.getChildAt(0).findViewById(R.id.button_up).getVisibility());
                assertEquals(View.VISIBLE, listView.getChildAt(0).findViewById(R.id.button_down).getVisibility());
                assertEquals(View.VISIBLE, listView.getChildAt(0).findViewById(R.id.button_edit).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_check).getVisibility());
            }
            {
                onView(withId(R.id.button_exit_edit_mode))
                        .perform(click());
            }
            {   // Check visiblity
                assertEquals(View.VISIBLE, activity.findViewById(R.id.button_enter_edit_mode).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.button_exit_edit_mode).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_up).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_down).getVisibility());
                assertEquals(View.GONE, listView.getChildAt(0).findViewById(R.id.button_edit).getVisibility());
                assertEquals(View.VISIBLE, listView.getChildAt(0).findViewById(R.id.button_check).getVisibility());
            }
        }
    }

    /**
     * Test change order of items. [0,1,2] -> [1,2,0]
     */
    public void testChangeOrder() throws Throwable{
        List<CheckListEntry> entries;
        {   // Creating dummy data.
            clearData();
            entries = createTestData(3, 4);
        }
        final FastCheckListActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        final ListView listView = (ListView) activity.findViewById(R.id.list_entries);
        {   // Check number of entries
            assertEquals(3,listView.getCount());
        }
        {   // Check items have correct order after sort.
            assertEquals(entries.get(0).getTitle(), ((CheckListEntry)listView.getItemAtPosition(0)).getTitle());
            assertEquals(entries.get(1).getTitle(), ((CheckListEntry)listView.getItemAtPosition(1)).getTitle());
            assertEquals(entries.get(2).getTitle(), ((CheckListEntry)listView.getItemAtPosition(2)).getTitle());
        }
        {
            {   // Enter edit mode
                onView(withId(R.id.button_enter_edit_mode))
                        .perform(click());
            }
            {   // Sort up
                setSelection(listView, 1);
                CheckListEntry entry = ((CheckListEntry)listView.getItemAtPosition(1));
                onView(allOf(withId(R.id.button_up), hasSibling(withText(entry.getTitle()))))
                    .perform(click());
            }
            {   // Sort down
                setSelection(listView, 1);
                CheckListEntry entry = ((CheckListEntry)listView.getItemAtPosition(1));
                onView(allOf(withId(R.id.button_down), hasSibling(withText(entry.getTitle()))))
                        .perform(click());
            }
            {   // Exit edit mode
                onView(withId(R.id.button_exit_edit_mode))
                        .perform(click());
            }
        }
        {   // Check items have correct order after sort.
            assertEquals(entries.get(1).getTitle(), ((CheckListEntry)listView.getItemAtPosition(0)).getTitle());
            assertEquals(entries.get(2).getTitle(), ((CheckListEntry)listView.getItemAtPosition(1)).getTitle());
            assertEquals(entries.get(0).getTitle(), ((CheckListEntry)listView.getItemAtPosition(2)).getTitle());
        }
    }

    private void setSelection(final ListView listView, final int position) throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(position);
            }
        });
    }
}
