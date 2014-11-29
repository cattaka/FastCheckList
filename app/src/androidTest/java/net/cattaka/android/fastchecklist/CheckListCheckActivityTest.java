
package net.cattaka.android.fastchecklist;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.test.BaseTestCase;

import java.util.List;

public class CheckListCheckActivityTest extends BaseTestCase<CheckListCheckActivity> {
    public CheckListCheckActivityTest() {
        super(CheckListCheckActivity.class);
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity() {
        CheckListCheckActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by back key
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test start and exit.
     */
    public void testStartAndCancelActivity() throws Throwable {
        final CheckListCheckActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by click button_ok
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.button_cancel).performClick();
                }
            });
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test screen transition to the edit screen.
     */
    public void testCheckAndRegister() throws Throwable{
        List<CheckListEntry> entries;
        {   // Creating dummy data.
            clearData();
            entries = createTestData(3, 4);
        }
        CheckListEntry testTarget = entries.get(1);
        {   // Check history is empty
            OpenHelper openHelper = new OpenHelper(mContext);
            List<CheckListHistory> histories = openHelper.findHistory(testTarget.getId());
            assertEquals(0, histories.size());
        }
        {   // set activity intent
            Intent intent = new Intent();
            intent.putExtra(CheckListCheckActivity.EXTRA_TARGET_ENTRY_ID, testTarget.getId());
            setActivityIntent(intent);
        }
        final CheckListCheckActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        final ListView listView = (ListView) activity.findViewById(R.id.list_items);
        {   // Test number of entries
            assertEquals(4, listView.getCount());
        }
        {
            for (int i=0;i<listView.getCount();i++) {
                {   // button_ok must be disable until all items are checked.
                    assertFalse(activity.findViewById(R.id.button_ok).isEnabled());
                }
                {   // Check items
                    final int t = i;
                    runTestOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.performItemClick(listView, t, -1);
                        }
                    });
                }
                getInstrumentation().waitForIdleSync();
            }
            {   // button_ok must be enable after all items are checked.
                assertTrue(activity.findViewById(R.id.button_ok).isEnabled());
            }
        }
        {   // Test finish by click button_ok
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.button_ok).performClick();
                }
            });
            assertTrue(activity.isFinishing());
        }
        {   // Check history has 1 item.
            OpenHelper openHelper = new OpenHelper(mContext);
            List<CheckListHistory> histories = openHelper.findHistory(testTarget.getId());
            assertEquals(1, histories.size());
        }
    }
}
