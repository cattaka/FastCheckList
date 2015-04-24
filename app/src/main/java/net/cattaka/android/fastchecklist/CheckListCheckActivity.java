package net.cattaka.android.fastchecklist;

import java.util.ArrayList;
import java.util.Date;

import net.cattaka.android.fastchecklist.adapter.MyAdapter;
import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CheckListCheckActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_TARGET_ENTRY_ID = "TARGET_ENTRY_ID";

    private ContextLogic mContextLogic = ContextLogicFactory.createContextLogic(this);

    private CheckListEntry mEntry;
    private MyAdapter mItemsAdapter;
    private OpenHelper mOpenHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check);

        mOpenHelper = mContextLogic.createOpenHelper();
        {
            Intent intent = getIntent();
            Long entryId = intent.getLongExtra(EXTRA_TARGET_ENTRY_ID, -1L);
            if (entryId != null && entryId >= 0L) {
                mEntry = mOpenHelper.findEntry(entryId, true);
            }
        }
        
        if (mEntry == null) {
            mEntry = new CheckListEntry();
            mEntry.setItems(new ArrayList<CheckListItem>());
            mEntry.setTitle("");
        }
        
        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        
        TextView textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText(mEntry.getTitle());
        mItemsAdapter = new MyAdapter(this, new ArrayList<CheckListItem>(mEntry.getItems()));
        ListView listView = (ListView) findViewById(R.id.list_items);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(mItemsAdapter);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		updateOkButton();
        	}
        });
        
		updateOkButton();
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) {
            CheckListHistory checkListHistory = new CheckListHistory();
            {   // 登録データの作成
                checkListHistory.setEntryId(mEntry.getId());
                checkListHistory.setDate(new Date());
            }
            {    // DBへの書き込みの実施
                mOpenHelper.registerHistory(checkListHistory);
            }
            finish();
        } else if (v.getId() == R.id.button_cancel) {
            finish();
        }
    }
    private void updateOkButton() {
    	boolean okFlag = true;
        ListView listView = (ListView) findViewById(R.id.list_items);
        SparseBooleanArray sba = listView.getCheckedItemPositions();
    	for (int i=0;i<mItemsAdapter.getCount();i++) {
    		if (!sba.get(i)) {
    			okFlag = false;
    			break;
    		}
    	}
        findViewById(R.id.button_ok).setEnabled(okFlag);
   }
}