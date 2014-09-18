package net.cattaka.android.fastchecklist.view;

import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ItemEntryDialog extends Dialog implements View.OnClickListener { 
	public interface OnItemEntryListener {
		public void onOk(CheckListItem item);
		public void onCancel();
	}
	
	private CheckListItem mItem;
	private OnItemEntryListener mOnItemEntryListener;
	
	public ItemEntryDialog(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_entry);
        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

		EditText editLabel = (EditText) findViewById(R.id.edit_label);
		editLabel.setText((mItem.getLabel() != null) ? mItem.getLabel() : "");
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_ok) {
			EditText editLabel = (EditText) findViewById(R.id.edit_label);
			mItem.setLabel(String.valueOf(editLabel.getText()));
			dismiss();
			if (mOnItemEntryListener != null) {
				mOnItemEntryListener.onOk(mItem);
			}
		} else if (v.getId() == R.id.button_cancel) {
			dismiss();
			if (mOnItemEntryListener != null) {
				mOnItemEntryListener.onCancel();
			}
		}
	}
	public void setup(OnItemEntryListener onItemEntryListener, CheckListItem item) {
		this.mOnItemEntryListener = onItemEntryListener;
		this.mItem = item;
	}
}
