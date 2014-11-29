package net.cattaka.android.fastchecklist.test;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.view.KeyEvent;

import net.cattaka.android.fastchecklist.FastCheckListActivity;
import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cattaka on 14/11/28.
 */
public class BaseTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    protected RenamingDelegatingContext mContext;

    public BaseTestCase(Class<T> tClass) {
        super(tClass);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        {   // Replace ContextLogicFactory to use RenamingDelegatingContext.
            ContextLogicFactory.replaceInstance(new ContextLogicFactory() {
                @Override
                public ContextLogic newInstance(Context context) {
                    return new ContextLogic(mContext);
                }
            });
        }
        {   // Unlock keyguard
            KeyguardManager km = (KeyguardManager) getInstrumentation().getTargetContext().getSystemService(Context.KEYGUARD_SERVICE);
            if (km.inKeyguardRestrictedInputMode()) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("net.cattaka.android.testutil", "net.cattaka.android.testutil.UnlockKeyguardActivity");
                try {
                    getInstrumentation().getTargetContext().startActivity(intent);
                    while (km.inKeyguardRestrictedInputMode()) {
                        SystemClock.sleep(100);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public void clearData() {
        OpenHelper openHelper = new OpenHelper(mContext);
        for (CheckListEntry entry : openHelper.findEntry()) {
            openHelper.deleteEntry(entry.getId());
        }
    }

    public List<CheckListEntry> createTestData(int entriesNum, int itemsNum) {
        List<CheckListEntry> entries = new ArrayList<CheckListEntry>();
        // Creating dummy data.
        OpenHelper openHelper = new OpenHelper(mContext);
        for (int i = 0; i < entriesNum; i++) {
            CheckListEntry entry = new CheckListEntry();
            entry.setTitle("Entry " + i);
            entry.setSort((long) i);
            entry.setItems(new ArrayList<CheckListItem>());
            for (int j = 0; j < itemsNum; j++) {
                CheckListItem item = new CheckListItem();
                item.setLabel("Item " + j);
                item.setSort((long)j);
                entry.getItems().add(item);
            }
            openHelper.registerEntry(entry);
            entries.add(entry);
        }
        return entries;
    }
}
