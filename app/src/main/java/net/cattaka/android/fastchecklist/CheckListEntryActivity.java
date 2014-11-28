package net.cattaka.android.fastchecklist;

import java.util.ArrayList;
import java.util.List;

import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.core.ContextLogic;
import net.cattaka.android.fastchecklist.core.ContextLogicFactory;
import net.cattaka.android.fastchecklist.db.OpenHelper;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import net.cattaka.android.fastchecklist.view.ItemEntryDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CheckListEntryActivity extends Activity implements
        View.OnClickListener {
    public static final String EXTRA_TARGET_ENTRY_ID = "TARGET_ENTRY_ID";
    public static final int DIALOG_TRASH = 3;

    class AdapterEx extends ArrayAdapter<CheckListItem> {
        public AdapterEx(List<CheckListItem> items) {
            super(CheckListEntryActivity.this, R.layout.layout_entry_item,
                    items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckListItem item = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater
                        .from(CheckListEntryActivity.this);
                convertView = inflater
                        .inflate(R.layout.layout_entry_item, null);
                convertView.setOnClickListener(onClickListenerForItems);
                convertView.findViewById(R.id.button_up).setOnClickListener(
                        onClickListenerForItems);
                convertView.findViewById(R.id.button_down).setOnClickListener(
                        onClickListenerForItems);
                convertView.findViewById(R.id.button_trash).setOnClickListener(
                        onClickListenerForItems);
            }
            convertView.setTag(position);
            convertView.findViewById(R.id.button_up).setTag(position);
            convertView.findViewById(R.id.button_down).setTag(position);
            convertView.findViewById(R.id.button_trash).setTag(position);

            TextView textLabel = (TextView) convertView
                    .findViewById(R.id.text_label);
            textLabel.setText(item.getLabel());
            return convertView;
        }
    }

    private ContextLogic mContextLogic = ContextLogicFactory.createContextLogic(this);

    private CheckListEntry mEntry;
    private AdapterEx mItemsAdapter;
    private int mTargetEntryIndex;
    private View.OnClickListener onClickListenerForItems = new View.OnClickListener() {
        public void onClick(View v) {
            int position;
            {
                if (!(v.getTag() instanceof Integer)) {
                    return;
                }
                position = (Integer) v.getTag();
                if (position < 0 || mItemsAdapter.getCount() <= position) {
                    return;
                }
            }
            mTargetEntryIndex = position;

            if (v.getId() == R.id.button_up) {
                if (position > 0) {
                    CheckListItem item = mItemsAdapter.getItem(position);
                    mItemsAdapter.remove(item);
                    mItemsAdapter.insert(item, position - 1);
                }
            } else if (v.getId() == R.id.button_down) {
                if (position < mItemsAdapter.getCount() - 1) {
                    CheckListItem item = mItemsAdapter.getItem(position);
                    mItemsAdapter.remove(item);
                    mItemsAdapter.insert(item, position + 1);
                }
            } else if (v.getId() == R.id.button_trash) {
                showDialog(DIALOG_TRASH);
            } else {
                CheckListItem item = mItemsAdapter.getItem(position);
                ItemEntryDialog dialog = new ItemEntryDialog(
                        CheckListEntryActivity.this);
                dialog.setTitle(R.string.dialog_title_change_label);
                dialog.setup(new ItemEntryDialog.OnItemEntryListener() {
                    @Override
                    public void onOk(CheckListItem item) {
                        mItemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {
                    }
                }, item);
                dialog.show();
            }
        };
    };

    private OpenHelper mOpenHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_entry);

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

        findViewById(R.id.button_add_item).setOnClickListener(this);
        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

        EditText editTitle = (EditText) findViewById(R.id.edit_title);
        editTitle.setText(mEntry.getTitle());
        mItemsAdapter = new AdapterEx(new ArrayList<CheckListItem>(
                mEntry.getItems()));
        ListView listView = (ListView) findViewById(R.id.list_items);
        listView.setAdapter(mItemsAdapter);
    }

    private void deleteItem(int pos) {
        if (pos < 0 || mItemsAdapter.getCount() <= pos) {
            return;
        }
        CheckListItem item = mItemsAdapter.getItem(pos);
        mItemsAdapter.remove(item);
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_item) {
            ItemEntryDialog dialog = new ItemEntryDialog(this);
            dialog.setTitle(R.string.dialog_title_input_label);
            dialog.setup(new ItemEntryDialog.OnItemEntryListener() {
                @Override
                public void onOk(CheckListItem item) {
                    mItemsAdapter.add(item);
                    mItemsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancel() {
                }
            }, new CheckListItem());
            dialog.show();
        } else if (v.getId() == R.id.button_ok) {
            {
                EditText editTitle = (EditText) findViewById(R.id.edit_title);
                String title = String.valueOf(editTitle.getText());
                mEntry.setTitle(title);
            }
            { // 登録用itemデータの作成
                mEntry.getItems().clear();
                for (int i = 0; i < mItemsAdapter.getCount(); i++) {
                    CheckListItem item = mItemsAdapter.getItem(i);
                    mEntry.getItems().add(item);
                }
            }
            { // DBへの書き込みの実施
                mOpenHelper.registerEntry(mEntry);
            }
            finish();
        } else if (v.getId() == R.id.button_cancel) {
            finish();
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_TRASH: {
            String title = getResources().getString(R.string.dialog_title_confirm);
            String message = getResources().getString(R.string.msg_delete_confirm);
            CharSequence messageSec = Html.fromHtml(message);
            
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (mTargetEntryIndex >= 0) {
                            deleteItem(mTargetEntryIndex);
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
}