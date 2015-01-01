package net.cattaka.android.fastchecklist.core;

import android.content.Context;

/**
 * Created by cattaka on 14/11/28.
 */
public class ContextLogicFactory {
    static ContextLogicFactory INSTANCE = new ContextLogicFactory();
    public ContextLogic newInstance(Context context) {
        return new ContextLogic(context);
    }

    public static ContextLogic createContextLogic(Context context) {
        return INSTANCE.newInstance(context);
    }

    public static void replaceInstance(ContextLogicFactory INSTANCE) {
        ContextLogicFactory.INSTANCE = INSTANCE;
    }
}
