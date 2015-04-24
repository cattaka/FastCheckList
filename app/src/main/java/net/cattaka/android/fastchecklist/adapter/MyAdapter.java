package net.cattaka.android.fastchecklist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.model.CheckListItem;

import java.util.List;

/**
* Created by takao on 2015/04/24.
*/
public class MyAdapter extends ArrayAdapter<CheckListItem> {
    public MyAdapter(Context context, List<CheckListItem> items) {
        super(context, R.layout.layout_check_item, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckListItem item = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_check_item, null);
        }
        convertView.setTag(position);

        CheckedTextView textLabel = (CheckedTextView) convertView;
        textLabel.setText(item.getLabel());
        return convertView;
    }
}
