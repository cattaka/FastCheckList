
package net.cattaka.android.fastchecklist.db;

import java.io.File;

import net.cattaka.android.fastchecklist.model.CheckListEntry;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

public class OpenHelperTest extends InstrumentationTestCase {
    private Context mContext;

    private OpenHelper mOpenHelper;

    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        mOpenHelper = new OpenHelper(mContext, "mydb");

        File file = mContext.getDatabasePath("myfile");
        Log.d("Debug", file.toString());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mOpenHelper.close();
        mOpenHelper = null;
        mContext = null;
    }

    public void testFind() {
        assertEquals(1, mOpenHelper.findEntry().size());

        CheckListEntry entry = new CheckListEntry();
        entry.setTitle("title");
        mOpenHelper.registerEntry(entry);
        assertEquals(2, mOpenHelper.findEntry().size());
    }

    public void testRegister() {
        assertEquals(1, mOpenHelper.findEntry().size());

        for (int i = 0; i < 10; i++) {
            CheckListEntry entry = new CheckListEntry();
            entry.setTitle("title" + i);
            mOpenHelper.registerEntry(entry);
        }
        assertEquals(11, mOpenHelper.findEntry().size());
    }
}
