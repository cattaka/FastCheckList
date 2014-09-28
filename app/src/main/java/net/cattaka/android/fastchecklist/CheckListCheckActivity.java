package net.cattaka.android.fastchecklist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

public class CheckListCheckActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_TARGET_ENTRY_ID = "TARGET_ENTRY_ID";
    class AdapterEx extends ArrayAdapter<CheckListItem> {
        public AdapterEx(List<CheckListItem> items) {
            super(CheckListCheckActivity.this, R.layout.layout_check_item, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckListItem item = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(CheckListCheckActivity.this);
                convertView = inflater.inflate(R.layout.layout_check_item, null);
            }
            convertView.setTag(position);
            
            CheckedTextView textLabel = (CheckedTextView) convertView;
            textLabel.setText(item.getLabel());
            return convertView;
        }
    }
    
    private CheckListEntry mEntry;
    private AdapterEx mItemsAdapter;
    private OpenHelper mOpenHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check);

        mOpenHelper = new OpenHelper(this);
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
        mItemsAdapter = new AdapterEx(new ArrayList<CheckListItem>(mEntry.getItems()));
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