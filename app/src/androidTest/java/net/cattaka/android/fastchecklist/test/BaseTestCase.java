package net.cattaka.android.fastchecklist.test;

import android.app.Activity;
import android.content.Context;
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
    }

    public void clearData() {
        OpenHelper openHelper = new OpenHelper(mContext);
        for (CheckListEntry entry : openHelper.findEntry()) {
            openHelper.deleteEntry(entry.getId());
        }
    }

    public void createTestData(int entries, int items) {
        // Creating dummy data.
        OpenHelper openHelper = new OpenHelper(mContext);
        for (int i = 0; i < entries; i++) {
            CheckListEntry entry = new CheckListEntry();
            entry.setTitle("Entry " + i);
            entry.setSort((long) i);
            entry.setItems(new ArrayList<CheckListItem>());
            for (int j = 0; j < items; j++) {
                CheckListItem item = new CheckListItem();
                item.setLabel("Item " + j);
                item.setSort((long)j);
                entry.getItems().add(item);
            }
            openHelper.registerEntry(entry);
        }
    }
}
