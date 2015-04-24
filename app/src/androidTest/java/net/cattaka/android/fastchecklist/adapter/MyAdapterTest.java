package net.cattaka.android.fastchecklist.adapter;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.CheckedTextView;

import net.cattaka.android.fastchecklist.model.CheckListItem;

import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

public class MyAdapterTest extends InstrumentationTestCase {
    public void testGetView() {
        List<CheckListItem> dummys = new ArrayList<>();
        {   // ダミーデータを作る
            dummys.add(new CheckListItem(1L, 1L, 1L, "Label1"));
            dummys.add(new CheckListItem(2L, 2L, 2L, "Label2"));
        }

        Context context = getInstrumentation().getTargetContext();
        MyAdapter sup = new MyAdapter(context, dummys);

        {   // 1つめのViewの表示内容を確認する
            View view = sup.getView(0, null, null);
            assertThat(view, is(Matchers.instanceOf(CheckedTextView.class)));
            assertThat(((CheckedTextView)view).getText().toString(), is("Label1"));
        }
        {   // 2つめのViewの表示内容を確認する
            View view = sup.getView(1, null, null);
            assertThat(view, is(Matchers.instanceOf(CheckedTextView.class)));
            assertThat(((CheckedTextView)view).getText().toString(), is("Label2"));
        }
    }
}