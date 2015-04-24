
package net.cattaka.android.fastchecklist.db;

import net.cattaka.android.fastchecklist.model.CheckListEntry;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.is;

public class OpenHelperTest extends InstrumentationTestCase {
    private OpenHelper mOpenHelper;

    @Before
    protected void before() throws Exception {
        super.setUp();
        Context context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        mOpenHelper = new OpenHelper(context);
    }

    @After
    protected void after() throws Exception {
        super.tearDown();
        mOpenHelper.close();
        mOpenHelper = null;
    }

    @Test
    public void testInsertSelect() {
        assertEquals(1, mOpenHelper.findEntry().size());

        CheckListEntry orig = new CheckListEntry();
        {   // INSERTする
            orig.setTitle("hoge");
            mOpenHelper.registerEntry(orig);
        }
        CheckListEntry dest;
        {   // SELECTする
            Long id = orig.getId();
            dest = mOpenHelper.findEntry(id, false);
        }
        {   // 確認する
            assertThat(dest.getTitle(), is("hoge"));
        }
    }

    @Test
    public void testFind() {
        assertEquals(1, mOpenHelper.findEntry().size());

        CheckListEntry entry = new CheckListEntry();
        entry.setTitle("title");
        mOpenHelper.registerEntry(entry);
        assertEquals(2, mOpenHelper.findEntry().size());
    }

    @Test
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
