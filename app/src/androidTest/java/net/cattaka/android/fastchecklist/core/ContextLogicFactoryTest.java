package net.cattaka.android.fastchecklist.core;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class ContextLogicFactoryTest extends InstrumentationTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextLogicFactory.replaceInstance(new ContextLogicFactory());
    }

    public void testNewInstance() {
        Context context = getInstrumentation().getTargetContext();
        ContextLogicFactory sup = new ContextLogicFactory();

        ContextLogic logic = sup.newInstance(context);
        assertEquals(ContextLogic.class, logic.getClass());
    }

    public void testReplaceInstance() {

        Context context = getInstrumentation().getTargetContext();
        ContextLogicFactory sup = new DummyContextLogicFactory();

        ContextLogic logic = sup.newInstance(context);
        assertEquals(DummyContextLogic.class, logic.getClass());
    }

    public static class DummyContextLogic extends ContextLogic {
        public DummyContextLogic(Context context) {
            super(context);
        }
    }

    public static class DummyContextLogicFactory extends ContextLogicFactory {
        @Override
        public ContextLogic newInstance(Context context) {
            return new DummyContextLogic(context);
        }
    }
}