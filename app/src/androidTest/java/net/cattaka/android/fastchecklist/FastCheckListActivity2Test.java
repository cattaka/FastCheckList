
package net.cattaka.android.fastchecklist;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.view.KeyEvent;
import android.widget.ListView;

import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import net.cattaka.android.fastchecklist.test.BaseTestCase;

import java.util.ArrayList;

public class FastCheckListActivity2Test extends BaseTestCase<FastCheckListActivity> {
    public FastCheckListActivity2Test() {
        super(FastCheckListActivity.class);
    }

    public void testStartAndExitActivity() {
        FastCheckListActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by back key
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            assertTrue(activity.isFinishing());
        }
    }

    public void testShowThreeDataAndScreenTransition() throws Throwable{
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
}
