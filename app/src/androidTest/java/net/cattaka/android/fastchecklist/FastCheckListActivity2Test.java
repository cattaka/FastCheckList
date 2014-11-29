
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
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.getChildAt(0).findViewById(R.id.button_check).performClick();
                    }
                });
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
            assertEquals(3,listView.getCount());
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
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.button_enter_edit_mode).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
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
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.button_exit_edit_mode).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
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
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.button_enter_edit_mode).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
            }
            {   // Sort up
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.getChildAt(1).findViewById(R.id.button_up).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
            }
            {   // Sort down
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.getChildAt(1).findViewById(R.id.button_down).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
            }
            {   // Exit edit mode
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.button_exit_edit_mode).performClick();
                    }
                });
                getInstrumentation().waitForIdleSync();
            }
        }
        {   // Check items have correct order after sort.
            assertEquals(entries.get(1).getTitle(), ((CheckListEntry)listView.getItemAtPosition(0)).getTitle());
            assertEquals(entries.get(2).getTitle(), ((CheckListEntry)listView.getItemAtPosition(1)).getTitle());
            assertEquals(entries.get(0).getTitle(), ((CheckListEntry)listView.getItemAtPosition(2)).getTitle());
        }
    }
}
