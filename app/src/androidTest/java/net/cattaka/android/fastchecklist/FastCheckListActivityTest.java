
package net.cattaka.android.fastchecklist;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;

import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.test.BaseTestCase;

public class FastCheckListActivityTest extends BaseTestCase<FastCheckListActivity> {
    public FastCheckListActivityTest() {
        super(FastCheckListActivity.class);
    }

    public void testStartActivity() {
        FastCheckListActivity activity = getActivity();
        activity.finish();
    }
}
