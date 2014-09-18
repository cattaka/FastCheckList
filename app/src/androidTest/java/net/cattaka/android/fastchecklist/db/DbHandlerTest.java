
package net.cattaka.android.fastchecklist.db;

import java.io.File;

import net.cattaka.android.fastchecklist.model.CheckListEntry;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

public class DbHandlerTest extends InstrumentationTestCase {
    private Context mContext;

    private DbHandler mDbHandler;

    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        mDbHandler = new DbHandler(mContext, "mydb");

        File file = mContext.getDatabasePath("myfile");
        Log.d("Debug", file.toString());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDbHandler.closeDatabase();
        mDbHandler = null;
        mContext = null;
    }

    public void testFind() {
        mDbHandler.openWritableDatabase();

        assertEquals(0, mDbHandler.findEntry().size());

        CheckListEntry entry = new CheckListEntry();
        entry.setTitle("title");
        mDbHandler.registerEntry(entry);
        assertEquals(1, mDbHandler.findEntry().size());
    }

    public void testRegister() {
        mDbHandler.openWritableDatabase();

        assertEquals(0, mDbHandler.findEntry().size());

        for (int i = 0; i < 10; i++) {
            CheckListEntry entry = new CheckListEntry();
            entry.setTitle("title" + i);
            mDbHandler.registerEntry(entry);
        }
        assertEquals(10, mDbHandler.findEntry().size());

        mDbHandler.closeDatabase();
        mDbHandler.openWritableDatabase();
        assertEquals(0, mDbHandler.findEntry().size());
    }
}
