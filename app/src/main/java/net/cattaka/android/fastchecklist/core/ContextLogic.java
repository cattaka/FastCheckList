package net.cattaka.android.fastchecklist.core;

import android.content.Context;

import net.cattaka.android.fastchecklist.db.OpenHelper;

/**
 * Created by cattaka on 14/11/28.
 */
public class ContextLogic {
    protected Context mContext;

    public ContextLogic(Context context) {
        mContext = context;
    }

    public OpenHelper createOpenHelper() {
        return new OpenHelper(mContext);
    }
}
