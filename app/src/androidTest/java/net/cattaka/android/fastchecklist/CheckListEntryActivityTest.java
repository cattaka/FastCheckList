
package net.cattaka.android.fastchecklist;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;

import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.test.BaseTestCase;

import java.util.List;

public class CheckListEntryActivityTest extends BaseTestCase<CheckListEntryActivity> {
    public CheckListEntryActivityTest() {
        super(CheckListEntryActivity.class);
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity_new() {
        CheckListEntryActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by back key
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity() {
        List<CheckListEntry> entries;
        {   // Creating dummy data.
            clearData();
            entries = createTestData(3, 4);
        }
        CheckListEntry testTarget = entries.get(1);
        {   // set activity intent
            Intent intent = new Intent();
            intent.putExtra(CheckListEntryActivity.EXTRA_TARGET_ENTRY_ID, testTarget.getId());
            setActivityIntent(intent);
        }
        CheckListEntryActivity activity = getActivity();
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
        List<CheckListEntry> entries;
        {   // Creating dummy data.
            clearData();
            entries = createTestData(3, 4);
        }
        CheckListEntry testTarget = entries.get(1);
        {   // set activity intent
            Intent intent = new Intent();
            intent.putExtra(CheckListEntryActivity.EXTRA_TARGET_ENTRY_ID, testTarget.getId());
            setActivityIntent(intent);
        }
        final CheckListEntryActivity activity = getActivity();
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
     * Test create new item.
     */
    public void testCreateNewItem() throws Throwable {
        {   // clear all data.
            clearData();
        }
        final CheckListEntryActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Input title to EditText
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EditText editTitle = (EditText) activity.findViewById(R.id.edit_title);
                    editTitle.setText("Test Title");
                }
            });
        }
        {   // Test finish by button_ok
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.button_ok).performClick();
                }
            });
            assertTrue(activity.isFinishing());
        }
        {   // Check new item is added on the tail.
            OpenHelper openHelper = new OpenHelper(mContext);
            List<CheckListEntry> entries = openHelper.findEntry();
            CheckListEntry entry = entries.get(entries.size() - 1);
            assertEquals("Test Title", entry.getTitle());
        }
    }

}
