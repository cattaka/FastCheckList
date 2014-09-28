package net.cattaka.android.fastchecklist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.util.ContextUtil;

public class FastCheckListActivity extends Activity
    implements View.OnClickListener
{
    private static final int DIALOG_ABOUT = 2;
    private static final int DIALOG_TRASH = 3;

    private AdapterEx mEntriesAdapter;
    private boolean mEditModeFlag = false;

    // UI系
    private int mTargetEntryIndex;

    private OpenHelper mOpenHelper;

    class AdapterEx extends ArrayAdapter<CheckListEntry> {
        public AdapterEx(List<CheckListEntry> entries) {
            super(FastCheckListActivity.this, R.layout.layout_main_item, entries);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckListEntry entry = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(FastCheckListActivity.this);
                convertView = inflater.inflate(R.layout.layout_main_item, null);
                convertView.setOnClickListener(mOnClickListenerForEntries);
                convertView.findViewById(R.id.button_trash).setOnClickListener(mOnClickListenerForEntries);
                convertView.findViewById(R.id.button_up).setOnClickListener(mOnClickListenerForEntries);
                convertView.findViewById(R.id.button_down).setOnClickListener(mOnClickListenerForEntries);
                convertView.findViewById(R.id.button_edit).setOnClickListener(mOnClickListenerForEntries);
                convertView.findViewById(R.id.button_check).setOnClickListener(mOnClickListenerForEntries);
            }
            convertView.setTag(position);
            convertView.findViewById(R.id.button_trash).setTag(position);
            convertView.findViewById(R.id.button_up).setTag(position);
            convertView.findViewById(R.id.button_down).setTag(position);
            convertView.findViewById(R.id.button_edit).setTag(position);
            convertView.findViewById(R.id.button_check).setTag(position);
            
            convertView.findViewById(R.id.button_trash).setVisibility((mEditModeFlag) ? View.VISIBLE : View.GONE);
            convertView.findViewById(R.id.button_up).setVisibility((mEditModeFlag) ? View.VISIBLE : View.GONE);
            convertView.findViewById(R.id.button_down).setVisibility((mEditModeFlag) ? View.VISIBLE : View.GONE);
            convertView.findViewById(R.id.button_edit).setVisibility((mEditModeFlag) ? View.VISIBLE : View.GONE);
            convertView.findViewById(R.id.button_check).setVisibility((!mEditModeFlag) ? View.VISIBLE : View.GONE);
            
            TextView textLabel = (TextView) convertView.findViewById(R.id.text_label);
            textLabel.setText(entry.getTitle());
            return convertView;
        }
    }

    private View.OnClickListener mOnClickListenerForEntries = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position;
            {
                if (!(v.getTag() instanceof Integer)) {
                    return;
                }
                position = (Integer) v.getTag();
                if (position < 0 || mEntriesAdapter.getCount() <= position) {
                    return;
                }
            }
            mTargetEntryIndex = position;
            if (v.getId() == R.id.button_up) {
                swapEntries(position, position - 1);
            } else if (v.getId() == R.id.button_down) {
                swapEntries(position, position + 1);
            } else if (v.getId() == R.id.button_check) {
                CheckListEntry entry = mEntriesAdapter.getItem(position);
                Intent intent = new Intent(FastCheckListActivity.this, CheckListCheckActivity.class);
                intent.putExtra(CheckListCheckActivity.EXTRA_TARGET_ENTRY_ID, entry.getId());
                startActivity(intent);
            } else if (v.getId() == R.id.button_edit) {
                CheckListEntry entry = mEntriesAdapter.getItem(position);
                Intent intent = new Intent(FastCheckListActivity.this, CheckListEntryActivity.class);
                intent.putExtra(CheckListEntryActivity.EXTRA_TARGET_ENTRY_ID, entry.getId());
                startActivity(intent);
            } else if (v.getId() == R.id.button_trash) {
                showDialog(DIALOG_TRASH);
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        mOpenHelper = new OpenHelper(this);

        findViewById(R.id.button_add_entry).setOnClickListener(this);
        findViewById(R.id.button_enter_edit_mode).setOnClickListener(this);
        findViewById(R.id.button_exit_edit_mode).setOnClickListener(this);
        
        changeEditMode(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        {
//            Intent intent = new Intent(this, TelnetSqliteService.class);
//            startService(intent);
        }
        List<CheckListEntry> entries;
        {
            entries = mOpenHelper.findEntry();
            if (entries == null) {
                entries = new ArrayList<CheckListEntry>();
            }
        }
        {
            mEntriesAdapter = new AdapterEx(entries);
            ListView listView = (ListView) findViewById(R.id.list_entries);
            listView.setAdapter(mEntriesAdapter);
        }
    }
    
    private void deleteEntries(int pos) {
        if (pos < 0 || mEntriesAdapter.getCount() <= pos) {
            return;
        }
        CheckListEntry entry = mEntriesAdapter.getItem(pos);
        mOpenHelper.deleteEntry(entry.getId());
        mEntriesAdapter.remove(entry);
    }
    private void swapEntries(int pos1, int pos2) {
        if (pos1 < 0 || mEntriesAdapter.getCount() <= pos1 || pos2 < 0 || mEntriesAdapter.getCount() <= pos2) {
            return;
        }
        CheckListEntry entry1 = mEntriesAdapter.getItem(pos1);
        CheckListEntry entry2 = mEntriesAdapter.getItem(pos2);
        mOpenHelper.swapEntriesSort(entry1.getId(), entry2.getId());

        mEntriesAdapter.remove(entry1);
        mEntriesAdapter.remove(entry2);
        if (pos1 < pos2) {
            mEntriesAdapter.insert(entry2, pos1);
            mEntriesAdapter.insert(entry1, pos2);
        } else {
            mEntriesAdapter.insert(entry1, pos2);
            mEntriesAdapter.insert(entry2, pos1);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            showDialog(DIALOG_ABOUT);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ABOUT: {
            String title = getResources().getString(R.string.app_name) + " "
                    + ContextUtil.getVersion(this);
            String message = getResources().getString(R.string.msg_about);
            CharSequence messageSec = Html.fromHtml(message);

            return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(title)
                    .setMessage(messageSec)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    /* User clicked Something so do some stuff */
                                }
                            }).create();
        }
        case DIALOG_TRASH: {
            String title = getResources().getString(R.string.dialog_title_confirm);
            String message = getResources().getString(R.string.msg_delete_confirm);
            CharSequence messageSec = Html.fromHtml(message);
            
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (mTargetEntryIndex >= 0) {
                            deleteEntries(mTargetEntryIndex);
                            mTargetEntryIndex = -1;
                            dialog.dismiss();
                        }
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                }
            };
            return new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title)
                    .setMessage(messageSec)
                    .setPositiveButton(android.R.string.ok,listener)
                    .setNegativeButton(android.R.string.cancel,listener)
                     .create();
        }
        }
        return super.onCreateDialog(id);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_entry) {
            Intent intent = new Intent(this, CheckListEntryActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button_enter_edit_mode) {
            changeEditMode(true);
        } else if (v.getId() == R.id.button_exit_edit_mode) {
            changeEditMode(false);
        }
    }
    private void changeEditMode(boolean editModeFlag) {
        if (editModeFlag) {
            mEditModeFlag = editModeFlag;
            findViewById(R.id.button_enter_edit_mode).setVisibility(View.GONE);
            findViewById(R.id.button_exit_edit_mode).setVisibility(View.VISIBLE);
            if (mEntriesAdapter != null) {
                mEntriesAdapter.notifyDataSetChanged();
            }
        } else {
            mEditModeFlag = editModeFlag;
            findViewById(R.id.button_enter_edit_mode).setVisibility(View.VISIBLE);
            findViewById(R.id.button_exit_edit_mode).setVisibility(View.GONE);
            if (mEntriesAdapter != null) {
                mEntriesAdapter.notifyDataSetChanged();
            }
        }
    }
}