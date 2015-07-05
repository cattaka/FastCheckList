
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CheckListEntryActivityTest extends BaseTestCase<CheckListEntryActivity> {
    public CheckListEntryActivityTest() {
        super(CheckListEntryActivity.class);
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity_new() throws Throwable {
        final CheckListEntryActivity activity = getActivity();
        assertFalse(activity.isFinishing());
        {   // Test finish by button_cancel
            onView(withId(R.id.button_cancel))
                    .perform(click());
            assertTrue(activity.isFinishing());
        }
    }

    /**
     * Test start and exit.
     */
    public void testStartAndExitActivity() throws Throwable {
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
        {   // Test finish by button_cancel
            onView(withId(R.id.button_cancel))
                    .perform(click());
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
            onView(withId(R.id.button_cancel))
                    .perform(click());
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
            onView(withId(R.id.edit_title))
                    .perform(typeText("Test Title"));
        }
        {   // Test finish by button_ok
            onView(withId(R.id.button_ok))
                    .perform(click());
            assertTrue(activity.isFinishing());
        }
        {   // Check new item is added on the tail.
            OpenHelper openHelper = mContextLogic.createOpenHelper();
            List<CheckListEntry> entries = openHelper.findEntry();
            CheckListEntry entry = entries.get(entries.size() - 1);
            assertEquals("Test Title", entry.getTitle());
        }
    }

}
