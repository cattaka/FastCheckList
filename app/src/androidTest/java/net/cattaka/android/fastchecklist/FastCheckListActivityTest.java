
package net.cattaka.android.fastchecklist;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;

public class FastCheckListActivityTest extends ActivityUnitTestCase<FastCheckListActivity> {
    private Context mContext;

    public FastCheckListActivityTest() {
        super(FastCheckListActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        setActivityContext(mContext);
    }

    public void testStartActivity() {
        Intent intent = new Intent(mContext, FastCheckListActivity.class);
        startActivity(intent, null, null);
        FastCheckListActivity activity = getActivity();
        getInstrumentation().callActivityOnCreate(activity, null);
        getInstrumentation().callActivityOnStart(activity);
        getInstrumentation().callActivityOnResume(activity);

        activity.finish();
    }

    public void testStartActivity2() {
        Intent intent = new Intent(mContext, FastCheckListActivity.class);
        startActivity(intent, null, null);

        FastCheckListActivity activity = getActivity();
        activity.finish();
    }
}
