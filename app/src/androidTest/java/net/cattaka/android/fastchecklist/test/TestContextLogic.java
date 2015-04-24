package net.cattaka.android.fastchecklist.test;

import android.content.Context;
import android.test.RenamingDelegatingContext;

import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.db.OpenHelper;

/**
 * Created by takao on 2015/04/24.
 */
public class TestContextLogic extends ContextLogic {
    private RenamingDelegatingContext mRdContext;
    public TestContextLogic(Context context) {
        super(context);
        mRdContext = new RenamingDelegatingContext(context, "test_");
    }

    @Override
    public OpenHelper createOpenHelper() {
        return new OpenHelper(mRdContext);
    }
}
